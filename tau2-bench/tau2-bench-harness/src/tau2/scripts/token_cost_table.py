#!/usr/bin/env python3
"""
Build assistant token comparison tables for Scala vs non-Scala runs.

The script compares full-domain runs for these models:
- deepseek-v3.2
- gpt-oss-120b
- minimax-m2.5

For each (model, domain) pair it reports:
- average prompt/completion/total assistant tokens per task
- p50 / p90 total assistant tokens per task

Definitions:
- "per task" means per task-trial / simulation run
- token usage is aggregated from assistant messages with a non-null `usage`
- Scala mode is inferred from `run()` tool usage, with the filename as fallback

Only full-domain runs are included so rows are comparable:
- airline: 50 tasks
- retail: 114 tasks
"""

from __future__ import annotations

import argparse
import math
import os
import sys
from collections import defaultdict
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass
from pathlib import Path

from loguru import logger
from rich.console import Console
from rich.progress import (
    BarColumn,
    MofNCompleteColumn,
    Progress,
    SpinnerColumn,
    TextColumn,
    TimeElapsedColumn,
)

logger.remove()
logger.add(sys.stderr, level="ERROR")

from tau2.data_model.message import AssistantMessage
from tau2.data_model.simulation import Results
from tau2.utils.utils import DATA_DIR

MODELS = ["deepseek-v3.2", "gpt-oss-120b", "minimax-m2.5"]
DOMAINS = ["airline", "retail"]


@dataclass(frozen=True)
class FileMetadata:
    """Lightweight metadata about one trajectory file."""

    path: Path
    domain: str
    task_count: int
    model_name: str
    scala_mode: bool
    has_simulations: bool


@dataclass(frozen=True)
class FileAggregation:
    """Per-file token aggregates."""

    key: tuple[str, str]
    scala_mode: bool
    prompt_tokens: tuple[int, ...]
    completion_tokens: tuple[int, ...]
    total_tokens: tuple[int, ...]
    zero_cost_missing_usage_messages: int


def detect_scala_mode(results: Results, path: Path) -> bool:
    """Infer scala mode from tool usage, with the filename as a fallback."""
    for sim in results.simulations[:10]:
        for message in sim.messages:
            if (
                isinstance(message, AssistantMessage)
                and message.tool_calls
                and any(tool_call.name == "run" for tool_call in message.tool_calls)
            ):
                return True
    return "scala" in path.stem


def iter_simulation_files(sim_dir: Path) -> list[Path]:
    """Return simulation files in a stable order."""
    return sorted(sim_dir.glob("*.json"))


def make_progress(console: Console) -> Progress:
    """Create a Rich progress instance for long-running scans."""
    return Progress(
        SpinnerColumn(),
        TextColumn("[progress.description]{task.description}"),
        BarColumn(),
        MofNCompleteColumn(),
        TimeElapsedColumn(),
        console=console,
    )


def resolve_worker_count(total: int, requested: int | None) -> int:
    """Choose a bounded worker count for file-parallel execution."""
    if total <= 1:
        return 1
    if requested is not None:
        return max(1, min(requested, total))
    cpu_count = os.cpu_count() or 4
    return max(1, min(total, cpu_count))


def inspect_file(path: Path) -> FileMetadata:
    """Load one file and return its filtering metadata."""
    results = Results.load(path)
    has_simulations = bool(results.simulations)
    domain = results.info.environment_info.domain_name
    model_name = (results.info.agent_info.llm or "unknown").split("/")[-1]
    task_count = len({sim.task_id for sim in results.simulations}) if has_simulations else 0
    scala_mode = detect_scala_mode(results, path) if has_simulations else False
    return FileMetadata(
        path=path,
        domain=domain,
        task_count=task_count,
        model_name=model_name,
        scala_mode=scala_mode,
        has_simulations=has_simulations,
    )


def collect_file_metadata(
    paths: list[Path], console: Console, show_progress: bool, workers: int | None
) -> tuple[list[FileMetadata], dict[str, int]]:
    """Inspect files once to collect filtering metadata."""
    metadata: list[FileMetadata] = []
    max_tasks_by_domain: dict[str, int] = defaultdict(int)
    empty_files = 0
    worker_count = resolve_worker_count(len(paths), workers)

    if show_progress:
        console.print(
            f"Inspecting simulation files with {worker_count} worker(s).",
            style="dim",
        )

    with ThreadPoolExecutor(max_workers=worker_count) as executor:
        futures = {executor.submit(inspect_file, path): path for path in paths}
        if show_progress:
            with make_progress(console) as progress:
                task_id = progress.add_task(
                    "Inspecting simulation files", total=len(paths)
                )
                for future in as_completed(futures):
                    item = future.result()
                    metadata.append(item)
                    if item.has_simulations:
                        max_tasks_by_domain[item.domain] = max(
                            max_tasks_by_domain[item.domain], item.task_count
                        )
                    else:
                        empty_files += 1
                    progress.update(
                        task_id, description=f"Inspected [cyan]{item.path.name}[/cyan]"
                    )
                    progress.advance(task_id)
        else:
            for future in as_completed(futures):
                item = future.result()
                metadata.append(item)
                if item.has_simulations:
                    max_tasks_by_domain[item.domain] = max(
                        max_tasks_by_domain[item.domain], item.task_count
                    )
                else:
                    empty_files += 1

    metadata.sort(key=lambda item: item.path.name)

    if show_progress:
        console.print(
            f"Discovered {len(paths)} JSON files, {len(paths) - empty_files} non-empty runs, "
            f"{empty_files} empty files.",
            style="dim",
        )
        if max_tasks_by_domain:
            domain_summary = ", ".join(
                f"{domain}={task_count} tasks"
                for domain, task_count in sorted(max_tasks_by_domain.items())
            )
            console.print(f"Full-domain baselines: {domain_summary}", style="dim")

    return metadata, dict(max_tasks_by_domain)


def select_candidate_files(
    metadata: list[FileMetadata],
    full_task_counts: dict[str, int],
    console: Console,
    show_progress: bool,
) -> list[FileMetadata]:
    """Keep only full-domain files for the requested models."""
    candidates = [
        item
        for item in metadata
        if item.has_simulations
        and item.model_name in MODELS
        and item.task_count == full_task_counts.get(item.domain)
    ]
    if show_progress:
        console.print(
            f"Selected {len(candidates)} full-domain files for models: {', '.join(MODELS)}",
            style="dim",
        )
    return candidates


def aggregate_file(item: FileMetadata) -> FileAggregation:
    """Aggregate assistant token metrics for one file."""
    results = Results.load(item.path)
    model_name = (results.info.agent_info.llm or "unknown").split("/")[-1]
    domain = results.info.environment_info.domain_name
    key = (model_name, domain)

    prompt_tokens: list[int] = []
    completion_tokens: list[int] = []
    total_tokens: list[int] = []
    zero_cost_missing_usage_messages = 0

    for sim in results.simulations:
        sim_prompt_tokens = 0
        sim_completion_tokens = 0
        for message in sim.messages:
            if not isinstance(message, AssistantMessage):
                continue
            if message.usage is not None:
                sim_prompt_tokens += int(message.usage.get("prompt_tokens", 0))
                sim_completion_tokens += int(message.usage.get("completion_tokens", 0))
            elif (message.cost or 0.0) == 0.0:
                zero_cost_missing_usage_messages += 1

        prompt_tokens.append(sim_prompt_tokens)
        completion_tokens.append(sim_completion_tokens)
        total_tokens.append(sim_prompt_tokens + sim_completion_tokens)

    return FileAggregation(
        key=key,
        scala_mode=item.scala_mode,
        prompt_tokens=tuple(prompt_tokens),
        completion_tokens=tuple(completion_tokens),
        total_tokens=tuple(total_tokens),
        zero_cost_missing_usage_messages=zero_cost_missing_usage_messages,
    )


def percentile(values: list[float] | list[int], p: float) -> float | int | None:
    """Nearest-rank percentile for a non-empty list."""
    if not values:
        return None
    sorted_values = sorted(values)
    rank = max(0, math.ceil(p * len(sorted_values)) - 1)
    return sorted_values[rank]


def format_int(value: int | float | None) -> str:
    """Format an integer-like value or return N/A."""
    if value is None:
        return "N/A"
    return f"{int(round(value)):,}"


def format_ratio(numerator: float | None, denominator: float | None) -> str:
    """Format a ratio if both sides are available and denominator is non-zero."""
    if numerator is None or denominator in (None, 0):
        return "N/A"
    return f"{numerator / denominator:.2f}x"


def build_tables(
    sim_dir: Path,
    console: Console,
    show_progress: bool = True,
    workers: int | None = None,
) -> tuple[str, str]:
    """Aggregate statistics and build markdown tables."""
    paths = iter_simulation_files(sim_dir)
    metadata, full_task_counts = collect_file_metadata(
        paths, console=console, show_progress=show_progress, workers=workers
    )
    candidates = select_candidate_files(
        metadata,
        full_task_counts=full_task_counts,
        console=console,
        show_progress=show_progress,
    )

    by_mode: dict[tuple[str, str, bool], dict[str, list[int]]] = defaultdict(
        lambda: {
            "prompt_tokens": [],
            "completion_tokens": [],
            "total_tokens": [],
        }
    )
    ignored_zero_cost_missing_usage_messages = 0

    worker_count = resolve_worker_count(len(candidates), workers)
    if show_progress:
        console.print(
            f"Aggregating token metrics with {worker_count} worker(s).",
            style="dim",
        )

    with ThreadPoolExecutor(max_workers=worker_count) as executor:
        futures = {executor.submit(aggregate_file, item): item for item in candidates}
        if show_progress:
            with make_progress(console) as progress:
                task_id = progress.add_task(
                    "Aggregating selected runs", total=len(candidates)
                )
                for future in as_completed(futures):
                    result = future.result()
                    stats = by_mode[(result.key[0], result.key[1], result.scala_mode)]
                    stats["prompt_tokens"].extend(result.prompt_tokens)
                    stats["completion_tokens"].extend(result.completion_tokens)
                    stats["total_tokens"].extend(result.total_tokens)
                    ignored_zero_cost_missing_usage_messages += (
                        result.zero_cost_missing_usage_messages
                    )
                    progress.update(
                        task_id,
                        description=(
                            f"Aggregated [green]{futures[future].path.name}[/green]"
                        ),
                    )
                    progress.advance(task_id)
        else:
            for future in as_completed(futures):
                result = future.result()
                stats = by_mode[(result.key[0], result.key[1], result.scala_mode)]
                stats["prompt_tokens"].extend(result.prompt_tokens)
                stats["completion_tokens"].extend(result.completion_tokens)
                stats["total_tokens"].extend(result.total_tokens)
                ignored_zero_cost_missing_usage_messages += (
                    result.zero_cost_missing_usage_messages
                )

    if show_progress:
        console.print(
            "Ignored assistant messages with missing usage only when their stored cost was 0.0 "
            f"({ignored_zero_cost_missing_usage_messages:,} messages).",
            style="dim",
        )

    average_columns = [
        "model",
        "domain",
        "avg prompt tokens/task (scala)",
        "avg completion tokens/task (scala)",
        "avg total tokens/task (scala)",
        "avg prompt tokens/task (non-Scala)",
        "avg completion tokens/task (non-Scala)",
        "avg total tokens/task (non-Scala)",
        "token ratio (scala/non)",
    ]
    distribution_columns = [
        "model",
        "domain",
        "p50 total tokens/task (scala)",
        "p90 total tokens/task (scala)",
        "p50 total tokens/task (non-Scala)",
        "p90 total tokens/task (non-Scala)",
    ]

    average_rows: list[dict[str, str]] = []
    distribution_rows: list[dict[str, str]] = []

    for model_name in MODELS:
        for domain in DOMAINS:
            scala = by_mode.get((model_name, domain, True))
            non_scala = by_mode.get((model_name, domain, False))

            scala_prompt = scala["prompt_tokens"] if scala else []
            scala_completion = scala["completion_tokens"] if scala else []
            scala_total = scala["total_tokens"] if scala else []

            non_prompt = non_scala["prompt_tokens"] if non_scala else []
            non_completion = non_scala["completion_tokens"] if non_scala else []
            non_total = non_scala["total_tokens"] if non_scala else []

            scala_avg_prompt = (
                sum(scala_prompt) / len(scala_prompt) if scala_prompt else None
            )
            scala_avg_completion = (
                sum(scala_completion) / len(scala_completion)
                if scala_completion
                else None
            )
            scala_avg_total = (
                sum(scala_total) / len(scala_total) if scala_total else None
            )
            non_avg_prompt = (
                sum(non_prompt) / len(non_prompt) if non_prompt else None
            )
            non_avg_completion = (
                sum(non_completion) / len(non_completion)
                if non_completion
                else None
            )
            non_avg_total = sum(non_total) / len(non_total) if non_total else None

            average_rows.append(
                {
                    "model": model_name,
                    "domain": domain,
                    "avg prompt tokens/task (scala)": format_int(scala_avg_prompt),
                    "avg completion tokens/task (scala)": format_int(
                        scala_avg_completion
                    ),
                    "avg total tokens/task (scala)": format_int(scala_avg_total),
                    "avg prompt tokens/task (non-Scala)": format_int(non_avg_prompt),
                    "avg completion tokens/task (non-Scala)": format_int(
                        non_avg_completion
                    ),
                    "avg total tokens/task (non-Scala)": format_int(non_avg_total),
                    "token ratio (scala/non)": format_ratio(
                        scala_avg_total, non_avg_total
                    ),
                }
            )

            distribution_rows.append(
                {
                    "model": model_name,
                    "domain": domain,
                    "p50 total tokens/task (scala)": format_int(
                        percentile(scala_total, 0.50)
                    ),
                    "p90 total tokens/task (scala)": format_int(
                        percentile(scala_total, 0.90)
                    ),
                    "p50 total tokens/task (non-Scala)": format_int(
                        percentile(non_total, 0.50)
                    ),
                    "p90 total tokens/task (non-Scala)": format_int(
                        percentile(non_total, 0.90)
                    ),
                }
            )

    return (
        format_markdown_table(
            "Average Assistant Tokens Per Task", average_columns, average_rows
        ),
        format_markdown_table(
            "Token Breakdown And Distribution Per Task",
            distribution_columns,
            distribution_rows,
        ),
    )


def make_parser() -> argparse.ArgumentParser:
    """Create the CLI parser."""
    parser = argparse.ArgumentParser(
        description="Build Scala vs non-Scala token comparison tables."
    )
    parser.add_argument(
        "--sim-dir",
        type=Path,
        default=DATA_DIR / "simulations",
        help="Directory containing simulation JSON files.",
    )
    parser.add_argument(
        "--quiet",
        action="store_true",
        help="Disable progress output and only print the final tables.",
    )
    parser.add_argument(
        "--workers",
        type=int,
        default=None,
        help="Number of parallel workers to use for file inspection and aggregation.",
    )
    return parser


def format_markdown_table(
    title: str, columns: list[str], rows: list[dict[str, str]]
) -> str:
    """Format a titled Markdown table."""
    lines = [
        f"## {title}",
        "",
        "| " + " | ".join(columns) + " |",
        "| " + " | ".join(["---"] * len(columns)) + " |",
    ]
    for row in rows:
        lines.append("| " + " | ".join(row[column] for column in columns) + " |")
    return "\n".join(lines)


def main() -> None:
    args = make_parser().parse_args()
    console = Console(file=sys.stderr)
    sim_dir = args.sim_dir
    if not sim_dir.exists():
        raise FileNotFoundError(f"Simulation directory not found: {sim_dir}")

    if not args.quiet:
        console.print(
            f"Building token tables from [cyan]{sim_dir}[/cyan]",
            style="bold",
        )

    average_table, distribution_table = build_tables(
        sim_dir,
        console=console,
        show_progress=not args.quiet,
        workers=args.workers,
    )
    print(average_table)
    print()
    print(distribution_table)


if __name__ == "__main__":
    main()
