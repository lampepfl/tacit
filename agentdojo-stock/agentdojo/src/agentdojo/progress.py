import contextvars
from collections.abc import Iterator
from contextlib import contextmanager

from rich.console import Console
from rich.progress import (
    BarColumn,
    MofNCompleteColumn,
    Progress,
    SpinnerColumn,
    TaskID,
    TextColumn,
    TimeElapsedColumn,
    TimeRemainingColumn,
)

_PROGRESS: contextvars.ContextVar[Progress | None] = contextvars.ContextVar("benchmark_progress", default=None)
_TASK_ID: contextvars.ContextVar[TaskID | None] = contextvars.ContextVar("benchmark_progress_task", default=None)


def make_progress(console: Console) -> Progress:
    return Progress(
        SpinnerColumn(),
        TextColumn("[bold blue]{task.description}"),
        BarColumn(),
        MofNCompleteColumn(),
        TextColumn("•"),
        TimeElapsedColumn(),
        TextColumn("ETA"),
        TimeRemainingColumn(),
        console=console,
        refresh_per_second=10,
    )


@contextmanager
def install(progress: Progress) -> Iterator[None]:
    token = _PROGRESS.set(progress)
    try:
        yield
    finally:
        _PROGRESS.reset(token)


@contextmanager
def track_suite(suite_name: str, total: int) -> Iterator[None]:
    progress = _PROGRESS.get()
    if progress is None:
        yield
        return
    task_id = progress.add_task(f"[bold]{suite_name}[/bold]", total=total)
    token = _TASK_ID.set(task_id)
    try:
        yield
    finally:
        _TASK_ID.reset(token)


def advance(description: str) -> None:
    progress = _PROGRESS.get()
    task_id = _TASK_ID.get()
    if progress is None or task_id is None:
        return
    progress.update(task_id, advance=1, description=description)
