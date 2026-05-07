#!/usr/bin/env python3
"""Render a Scala-call retry summary for tau2-bench simulations.

Mirrors the methodology of `tau2.scripts.scala_retry_table` (which works on
uncompressed JSON) but reads the `.json.zst` files shipped under
`data/simulations/`.

For each (model, domain) pair:

* a *Scala call* is a `run` tool call;
* a Scala call is a *compile-error call* if its tool response contains a
  Scala 3 compiler diagnostic header — i.e. matches
  ``(?m)^-- \\[E\\d+\\] [^\\n]*Error:`` (compilation, type, parse,
  naming, etc. — anything tagged with an `[E###]` ... `Error:` header,
  which excludes warnings);
* a Scala call is *successful* otherwise (runtime/tool exceptions after
  successful compilation still count as "successful" because the metric
  is about compilation only);
* the *retry count* of a successful call is the number of compile-error
  `run` calls immediately preceding it. The retry chain resets on a user
  message or any assistant message that does not include a `run` call.

Reported metrics:

* **Avg. Retries** — `total_retries / retried_successful_scala_calls`.
* **Calls w/ Retries (%)** — fraction of successful Scala calls whose
  retry count is non-zero.
"""
from __future__ import annotations

import argparse
import json
import re
import subprocess
from pathlib import Path

MODEL_LABELS = {
    "deepseek-v3.2": "DeepSeek V3.2",
    "gpt-oss-120b": "gpt-oss-120b",
    "minimax-m2.5": "MiniMax M2.5",
}
MODELS = list(MODEL_LABELS)
DOMAINS = ["airline", "retail"]

COMPILE_HEADER_RE = re.compile(r"(?m)^-- \[E\d+\] [^\n]*Error:")


def load_json(path: Path) -> dict:
    if path.suffix == ".zst":
        raw = subprocess.check_output(["zstd", "-d", "-c", "-q", str(path)])
        return json.loads(raw)
    return json.loads(path.read_text())


def find_scala_path(directory: Path, model: str, domain: str) -> Path | None:
    stem = f"{model}_{domain}_scala"
    for ext in (".json.zst", ".json"):
        p = directory / f"{stem}{ext}"
        if p.exists():
            return p
    return None


def aggregate(path: Path) -> tuple[int, int, int]:
    """Return (successful_calls, retried_successful_calls, total_retries)."""
    data = load_json(path)

    successful = 0
    retried_successful = 0
    total_retries = 0

    for sim in data["simulations"]:
        tool_by_id = {
            msg["id"]: msg for msg in sim["messages"] if msg.get("role") == "tool"
        }
        pending = 0
        for msg in sim["messages"]:
            role = msg.get("role")
            if role == "assistant":
                run_calls = [
                    tc for tc in (msg.get("tool_calls") or [])
                    if tc.get("name") == "run"
                ]
                if run_calls:
                    for tc in run_calls:
                        resp = tool_by_id.get(tc.get("id"))
                        content = (resp or {}).get("content") or ""
                        if COMPILE_HEADER_RE.search(content):
                            pending += 1
                        else:
                            successful += 1
                            total_retries += pending
                            if pending > 0:
                                retried_successful += 1
                            pending = 0
                else:
                    pending = 0
            elif role == "user":
                pending = 0

    return successful, retried_successful, total_retries


def fmt_avg(retries: int, retried: int) -> str:
    return f"{retries / retried:.2f}" if retried else "—"


def fmt_pct(retried: int, successful: int) -> str:
    return f"{100 * retried / successful:.2f}%" if successful else "—"


def print_table(rows: list[list[str]]) -> None:
    headers_top = ["Model", "Avg. Retries", "", "Calls w/ Retries", ""]
    headers_bot = ["", "airline", "retail", "airline", "retail"]
    widths = [
        max(len(headers_top[i]), len(headers_bot[i]),
            *(len(r[i]) for r in rows))
        for i in range(5)
    ]
    fmt = "  ".join(f"{{:<{w}}}" for w in widths)
    print(fmt.format(*headers_top))
    print(fmt.format(*headers_bot))
    print(fmt.format(*("-" * w for w in widths)))
    for r in rows:
        print(fmt.format(*r))


def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument(
        "dir",
        nargs="?",
        default="data/simulations",
        type=Path,
        help="Directory of simulation JSONs (default: data/simulations)",
    )
    args = ap.parse_args()

    rows = []
    for model in MODELS:
        cells: dict[str, tuple[int, int, int]] = {}
        for domain in DOMAINS:
            path = find_scala_path(args.dir, model, domain)
            cells[domain] = aggregate(path) if path else (0, 0, 0)
        s_a, r_a, tr_a = cells["airline"]
        s_r, r_r, tr_r = cells["retail"]
        rows.append([
            MODEL_LABELS[model],
            fmt_avg(tr_a, r_a),
            fmt_avg(tr_r, r_r),
            fmt_pct(r_a, s_a),
            fmt_pct(r_r, s_r),
        ])

    print("Scala-call retry summary "
          "(avg retries when retry needed; "
          "% of successful Scala calls that needed any retry)\n")
    print_table(rows)


if __name__ == "__main__":
    main()
