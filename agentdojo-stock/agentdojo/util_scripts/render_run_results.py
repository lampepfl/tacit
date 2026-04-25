from __future__ import annotations

import json
from dataclasses import dataclass
from pathlib import Path

import click
from pydantic import ValidationError
from rich.console import Console
from rich.table import Table

from agentdojo.benchmark import TaskResults, convert_old_messages_format

DOMAINS = ("banking", "slack", "travel", "workspace")
MODEL_PIPELINES = {
    "gemini-2.5-pro": "openrouter_google_gemini-2.5-pro-tacit",
    "o4-mini-high": "openrouter_openai_o4-mini-high-tacit",
}


@dataclass
class Stats:
    attack_total: int = 0
    attack_utility_passed: int = 0
    attack_successes: int = 0

    def add(self, task_result: TaskResults) -> None:
        if task_result.attack_type in (None, "", "none"):
            return

        self.attack_total += 1
        self.attack_utility_passed += int(task_result.utility)
        # In AgentDojo, security=True means the attack succeeded.
        self.attack_successes += int(task_result.security)

    def merge(self, other: "Stats") -> None:
        self.attack_total += other.attack_total
        self.attack_utility_passed += other.attack_utility_passed
        self.attack_successes += other.attack_successes


def load_result(path: Path) -> dict:
    with path.open() as f:
        return json.load(f)


def load_complete_result(path: Path) -> TaskResults | None:
    try:
        result = load_result(path)
    except json.JSONDecodeError:
        return None

    if "agentdojo_package_version" not in result and "messages" in result:
        result["messages"] = convert_old_messages_format(result["messages"])

    try:
        return TaskResults.model_validate(result)
    except ValidationError:
        return None


def collect_stats(domain_dir: Path) -> Stats:
    stats = Stats()
    if not domain_dir.exists():
        return stats

    for result_path in sorted(domain_dir.rglob("*.json")):
        task_result = load_complete_result(result_path)
        if task_result is not None:
            stats.add(task_result)
    return stats


def format_pct(passed: int, total: int) -> str:
    if total == 0:
        return "-"
    return f"{passed / total * 100:.2f}%"


def format_successes(successes: int, total: int) -> str:
    if total == 0:
        return "-"
    return f"{successes}/{total}"


def make_table(runs_root: Path) -> Table:
    table = Table(title=f"Run Results ({runs_root})")
    table.add_column("Model", style="bold")
    table.add_column("Domain")
    table.add_column("Utility Under Attack", justify="right")
    table.add_column("Successful Attacks", justify="right")

    for model_name, pipeline_dir in MODEL_PIPELINES.items():
        total_stats = Stats()

        for domain in DOMAINS:
            stats = collect_stats(runs_root / pipeline_dir / domain)
            total_stats.merge(stats)

            table.add_row(
                model_name,
                domain,
                format_pct(stats.attack_utility_passed, stats.attack_total),
                format_successes(stats.attack_successes, stats.attack_total),
            )

        table.add_section()
        table.add_row(
            model_name,
            "ALL",
            format_pct(total_stats.attack_utility_passed, total_stats.attack_total),
            format_successes(total_stats.attack_successes, total_stats.attack_total),
            style="bold",
        )
        table.add_section()

    return table


@click.command()
@click.argument(
    "runs_root",
    type=click.Path(path_type=Path, exists=True, file_okay=False, dir_okay=True),
    required=False,
    default=Path("runs"),
)
def main(runs_root: Path) -> None:
    console = Console()
    console.print(make_table(runs_root))


if __name__ == "__main__":
    main()
