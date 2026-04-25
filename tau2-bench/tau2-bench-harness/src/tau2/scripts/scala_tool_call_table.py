#!/usr/bin/env python3
"""
Build the Scala vs non-Scala tool-call comparison table for selected models.

The script reproduces the table with these columns:
- average Scala tool calls per task
- average implied native tool call per scala call
- Compilation error ratio
- average tool calls per task (non-Scala)

Only full-domain runs are included so rows are comparable:
- airline: 50 tasks
- retail: 114 tasks

In Scala mode, the assistant-facing tool is the explicit ``run(code)`` call.
Compilation errors are counted when the corresponding tool response contains a
Scala 3 compiler diagnostic header such as ``-- [E008] ... Error:``.
"""

from __future__ import annotations

import argparse
import os
import re
import sys
from concurrent.futures import ThreadPoolExecutor, as_completed
from collections import defaultdict
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

from tau2.data_model.message import AssistantMessage, ToolMessage
from tau2.data_model.simulation import Results
from tau2.utils.utils import DATA_DIR

MODELS = ["deepseek-v3.2", "gpt-oss-120b", "minimax-m2.5"]
DOMAINS = ["airline", "retail"]
COMPILE_HEADER_RE = re.compile(r"(?m)^-- \[E\d+\] [^\n]*Error:")


@dataclass(frozen=True)
class FileMetadata:
    """Lightweight metadata about a simulation file."""

    path: Path
    domain: str
    task_count: int
    model_name: str
    scala_mode: bool
    has_simulations: bool


@dataclass(frozen=True)
class FileAggregation:
    """Per-file aggregation output."""

    key: tuple[str, str]
    scala_mode: bool
    tool_calls: int
    implied_native_tool_calls: int
    compile_error_calls: int
    simulations: int


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


def build_rows(
    sim_dir: Path,
    console: Console,
    show_progress: bool = True,
    workers: int | None = None,
) -> list[dict[str, str]]:
    """Aggregate the comparison rows."""
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
    scala_stats: dict[tuple[str, str], dict[str, int]] = defaultdict(
        lambda: {
            "tool_calls": 0,
            "implied_native_tool_calls": 0,
            "compile_error_calls": 0,
            "simulations": 0,
        }
    )
    non_scala_stats: dict[tuple[str, str], dict[str, int]] = defaultdict(
        lambda: {"tool_calls": 0, "simulations": 0}
    )

    def aggregate_file(item: FileMetadata) -> FileAggregation:
        results = Results.load(item.path)
        model_name = (results.info.agent_info.llm or "unknown").split("/")[-1]
        domain = results.info.environment_info.domain_name
        key = (model_name, domain)
        tool_calls = 0
        implied_native_tool_calls = 0
        compile_error_calls = 0
        simulations = len(results.simulations)

        if item.scala_mode:
            for sim in results.simulations:
                tool_messages_by_id = {
                    message.id: message
                    for message in sim.messages
                    if isinstance(message, ToolMessage)
                }
                for message in sim.messages:
                    if not (
                        isinstance(message, AssistantMessage) and message.tool_calls
                    ):
                        continue
                    for tool_call in message.tool_calls:
                        if tool_call.name != "run":
                            continue
                        tool_calls += 1
                        response = tool_messages_by_id.get(tool_call.id)
                        if response:
                            implied_native_tool_calls += len(
                                response.implied_tool_calls or []
                            )
                        if response and COMPILE_HEADER_RE.search(response.content or ""):
                            compile_error_calls += 1
        else:
            for sim in results.simulations:
                for message in sim.messages:
                    if isinstance(message, AssistantMessage) and message.tool_calls:
                        tool_calls += len(message.tool_calls)

        return FileAggregation(
            key=key,
            scala_mode=item.scala_mode,
            tool_calls=tool_calls,
            implied_native_tool_calls=implied_native_tool_calls,
            compile_error_calls=compile_error_calls,
            simulations=simulations,
        )

    worker_count = resolve_worker_count(len(candidates), workers)
    if show_progress:
        console.print(
            f"Aggregating selected runs with {worker_count} worker(s).",
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
                    if result.scala_mode:
                        stats = scala_stats[result.key]
                        stats["tool_calls"] += result.tool_calls
                        stats["implied_native_tool_calls"] += (
                            result.implied_native_tool_calls
                        )
                        stats["compile_error_calls"] += result.compile_error_calls
                        stats["simulations"] += result.simulations
                    else:
                        stats = non_scala_stats[result.key]
                        stats["tool_calls"] += result.tool_calls
                        stats["simulations"] += result.simulations
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
                if result.scala_mode:
                    stats = scala_stats[result.key]
                    stats["tool_calls"] += result.tool_calls
                    stats["implied_native_tool_calls"] += (
                        result.implied_native_tool_calls
                    )
                    stats["compile_error_calls"] += result.compile_error_calls
                    stats["simulations"] += result.simulations
                else:
                    stats = non_scala_stats[result.key]
                    stats["tool_calls"] += result.tool_calls
                    stats["simulations"] += result.simulations

    rows: list[dict[str, str]] = []
    for model_name in MODELS:
        for domain in DOMAINS:
            key = (model_name, domain)
            scala = scala_stats.get(key)
            non_scala = non_scala_stats.get(key)
            rows.append(
                {
                    "model": model_name,
                    "domain": domain,
                    "average Scala tool calls per task": (
                        f"{scala['tool_calls'] / scala['simulations']:.3f}"
                        if scala and scala["simulations"]
                        else "N/A"
                    ),
                    "average implied native tool call per scala call": (
                        f"{scala['implied_native_tool_calls'] / scala['tool_calls']:.3f}"
                        if scala and scala["tool_calls"]
                        else "N/A"
                    ),
                    "Compilation error ratio": (
                        f"{scala['compile_error_calls'] / scala['tool_calls']:.2%}"
                        if scala and scala["tool_calls"]
                        else "N/A"
                    ),
                    "average tool calls per task (non-Scala)": (
                        f"{non_scala['tool_calls'] / non_scala['simulations']:.3f}"
                        if non_scala and non_scala["simulations"]
                        else "N/A"
                    ),
                }
            )
    return rows


def make_parser() -> argparse.ArgumentParser:
    """Create the CLI parser."""
    parser = argparse.ArgumentParser(
        description="Build the Scala vs non-Scala tool-call comparison table."
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
        help="Disable progress output and only print the final table.",
    )
    parser.add_argument(
        "--workers",
        type=int,
        default=None,
        help="Number of parallel workers to use for file inspection and aggregation.",
    )
    return parser


def format_markdown_table(rows: list[dict[str, str]]) -> str:
    """Format rows as a Markdown table."""
    columns = [
        "model",
        "domain",
        "average Scala tool calls per task",
        "average implied native tool call per scala call",
        "Compilation error ratio",
        "average tool calls per task (non-Scala)",
    ]
    lines = [
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
            f"Building Scala tool-call table from [cyan]{sim_dir}[/cyan]",
            style="bold",
        )

    rows = build_rows(
        sim_dir,
        console=console,
        show_progress=not args.quiet,
        workers=args.workers,
    )
    print(format_markdown_table(rows))


if __name__ == "__main__":
    main()
