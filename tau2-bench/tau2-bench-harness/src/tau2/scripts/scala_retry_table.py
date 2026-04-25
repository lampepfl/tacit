#!/usr/bin/env python3
"""
Build retry statistics for Scala `run()` tool calls.

Definitions:
- A "successful" Scala call is a `run()` call whose response does not contain a
  Scala compiler error diagnostic.
- A retry count is the number of compilation-errored `run()` calls immediately
  preceding a successful `run()` call.
- The retry chain resets if the assistant stops calling `run()` and sends a
  normal message, or if a new user turn begins.
- Runtime/tool exceptions after successful compilation still count as
  "successful" for this metric, because the retry definition is about
  compilation errors only.

For each (model, domain) pair the script reports:
- average retries when a retry is needed
- percentage of successful Scala calls that need retry
- ratio of compilation-error Scala calls to successful Scala calls
"""

from __future__ import annotations

import argparse
import sys
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass
from pathlib import Path

from loguru import logger
from rich.console import Console

logger.remove()
logger.add(sys.stderr, level="ERROR")

from tau2.data_model.message import AssistantMessage, ToolMessage, UserMessage
from tau2.data_model.simulation import Results
from tau2.scripts.scala_tool_call_table import (
    COMPILE_HEADER_RE,
    DOMAINS,
    MODELS,
    FileMetadata,
    collect_file_metadata,
    iter_simulation_files,
    make_progress,
    resolve_worker_count,
    select_candidate_files,
)
from tau2.utils.utils import DATA_DIR


@dataclass(frozen=True)
class FileAggregation:
    """Per-file retry aggregation output."""

    key: tuple[str, str]
    compile_error_scala_calls: int
    successful_scala_calls: int
    retried_successful_scala_calls: int
    total_retries: int


def aggregate_file(item: FileMetadata) -> FileAggregation:
    """Aggregate retry statistics for one Scala trajectory file."""
    results = Results.load(item.path)
    model_name = (results.info.agent_info.llm or "unknown").split("/")[-1]
    domain = results.info.environment_info.domain_name

    compile_error_scala_calls = 0
    successful_scala_calls = 0
    retried_successful_scala_calls = 0
    total_retries = 0

    for sim in results.simulations:
        tool_messages_by_id = {
            message.id: message
            for message in sim.messages
            if isinstance(message, ToolMessage)
        }
        pending_compile_error_retries = 0

        for message in sim.messages:
            if isinstance(message, AssistantMessage):
                run_tool_calls = [
                    tool_call
                    for tool_call in (message.tool_calls or [])
                    if tool_call.name == "run"
                ]
                if run_tool_calls:
                    for tool_call in run_tool_calls:
                        response = tool_messages_by_id.get(tool_call.id)
                        has_compile_error = bool(
                            response
                            and COMPILE_HEADER_RE.search(response.content or "")
                        )
                        if has_compile_error:
                            compile_error_scala_calls += 1
                            pending_compile_error_retries += 1
                        else:
                            successful_scala_calls += 1
                            total_retries += pending_compile_error_retries
                            if pending_compile_error_retries > 0:
                                retried_successful_scala_calls += 1
                            pending_compile_error_retries = 0
                else:
                    pending_compile_error_retries = 0
            elif isinstance(message, UserMessage):
                pending_compile_error_retries = 0

    return FileAggregation(
        key=(model_name, domain),
        compile_error_scala_calls=compile_error_scala_calls,
        successful_scala_calls=successful_scala_calls,
        retried_successful_scala_calls=retried_successful_scala_calls,
        total_retries=total_retries,
    )


def build_rows(
    sim_dir: Path,
    console: Console,
    show_progress: bool = True,
    workers: int | None = None,
) -> list[dict[str, str]]:
    """Aggregate retry rows across full-domain Scala runs."""
    paths = iter_simulation_files(sim_dir)
    metadata, full_task_counts = collect_file_metadata(
        paths,
        console=console,
        show_progress=show_progress,
        workers=workers,
    )
    candidates = [
        item
        for item in select_candidate_files(
            metadata,
            full_task_counts=full_task_counts,
            console=console,
            show_progress=show_progress,
        )
        if item.scala_mode
    ]

    retry_stats: dict[tuple[str, str], dict[str, int]] = {
        (model_name, domain): {
            "compile_error_scala_calls": 0,
            "successful_scala_calls": 0,
            "retried_successful_scala_calls": 0,
            "total_retries": 0,
        }
        for model_name in MODELS
        for domain in DOMAINS
    }

    worker_count = resolve_worker_count(len(candidates), workers)
    if show_progress:
        console.print(
            f"Aggregating Scala retry metrics with {worker_count} worker(s).",
            style="dim",
        )

    with ThreadPoolExecutor(max_workers=worker_count) as executor:
        futures = {executor.submit(aggregate_file, item): item for item in candidates}
        if show_progress:
            with make_progress(console) as progress:
                task_id = progress.add_task(
                    "Aggregating selected Scala runs", total=len(candidates)
                )
                for future in as_completed(futures):
                    result = future.result()
                    stats = retry_stats[result.key]
                    stats["compile_error_scala_calls"] += (
                        result.compile_error_scala_calls
                    )
                    stats["successful_scala_calls"] += result.successful_scala_calls
                    stats["retried_successful_scala_calls"] += (
                        result.retried_successful_scala_calls
                    )
                    stats["total_retries"] += result.total_retries
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
                stats = retry_stats[result.key]
                stats["compile_error_scala_calls"] += result.compile_error_scala_calls
                stats["successful_scala_calls"] += result.successful_scala_calls
                stats["retried_successful_scala_calls"] += (
                    result.retried_successful_scala_calls
                )
                stats["total_retries"] += result.total_retries

    rows: list[dict[str, str]] = []
    for model_name in MODELS:
        for domain in DOMAINS:
            stats = retry_stats[(model_name, domain)]
            compile_error_scala_calls = stats["compile_error_scala_calls"]
            successful_scala_calls = stats["successful_scala_calls"]
            retried_successful_scala_calls = stats["retried_successful_scala_calls"]
            total_retries = stats["total_retries"]
            rows.append(
                {
                    "model": model_name,
                    "domain": domain,
                    "successful scala calls": (
                        f"{successful_scala_calls:,}"
                        if successful_scala_calls
                        else "N/A"
                    ),
                    "average retries when a retry is needed": (
                        f"{total_retries / retried_successful_scala_calls:.4f}"
                        if retried_successful_scala_calls
                        else "N/A"
                    ),
                    "percentage of successful scala calls that need retry": (
                        f"{100 * retried_successful_scala_calls / successful_scala_calls:.2f}%"
                        if successful_scala_calls
                        else "N/A"
                    ),
                    "ratio of compilation errors / successful scala calls": (
                        f"{compile_error_scala_calls / successful_scala_calls:.4f}"
                        if successful_scala_calls
                        else "N/A"
                    ),
                }
            )
    return rows


def make_parser() -> argparse.ArgumentParser:
    """Create the CLI parser."""
    parser = argparse.ArgumentParser(
        description="Build Scala retry statistics tables."
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
        "average retries when a retry is needed",
        "percentage of successful scala calls that need retry",
        "ratio of compilation errors / successful scala calls",
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
            f"Building Scala retry table from [cyan]{sim_dir}[/cyan]",
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
