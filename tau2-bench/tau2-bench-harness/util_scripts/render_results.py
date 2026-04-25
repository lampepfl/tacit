#!/usr/bin/env python3
"""Render a summary table of tau2-bench simulation results.

Reads every `{model}_{domain}[_scala].json[.zst]` file under the given
directory (default: `data/simulations/`) and prints a pass^1 comparison
table for TACIT (scala) vs. baseline (plain), per (model, domain).

Zstd-compressed files are decompressed on the fly via the `zstd` CLI.
"""
from __future__ import annotations

import argparse
import json
import re
import subprocess
from pathlib import Path

MODELS = ["deepseek-v3.2", "gpt-oss-120b", "minimax-m2.5"]
DOMAINS = ["airline", "retail"]

FILENAME_RE = re.compile(
    r"^(?P<model>.+?)_(?P<domain>airline|retail)(?P<scala>_scala)?$"
)


def load_json(path: Path) -> dict:
    if path.suffix == ".zst":
        raw = subprocess.check_output(["zstd", "-d", "-c", "-q", str(path)])
        return json.loads(raw)
    return json.loads(path.read_text())


def avg_reward(path: Path) -> float:
    sims = load_json(path)["simulations"]
    return sum(
        1 if (s.get("reward_info", {}).get("reward") or 0) >= 1 - 1e-6 else 0
        for s in sims
    ) / len(sims)


def sim_stem(path: Path) -> str:
    """Strip .json and .json.zst suffixes to get the run identifier."""
    name = path.name
    if name.endswith(".json.zst"):
        return name[: -len(".json.zst")]
    if name.endswith(".json"):
        return name[: -len(".json")]
    return path.stem


def parse_filename(path: Path) -> tuple[str, str, str] | None:
    m = FILENAME_RE.match(sim_stem(path))
    if not m:
        return None
    return m["model"], m["domain"], "scala" if m["scala"] else "plain"


def print_table(headers: list[str], rows: list[list[str]]) -> None:
    widths = [
        max(len(str(h)), *(len(str(r[i])) for r in rows)) if rows else len(str(h))
        for i, h in enumerate(headers)
    ]
    fmt = "  ".join(f"{{:<{w}}}" for w in widths)
    print(fmt.format(*headers))
    print(fmt.format(*("-" * w for w in widths)))
    for r in rows:
        print(fmt.format(*(str(x) for x in r)))


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

    # Collect paths, preferring raw .json over .json.zst when both exist.
    paths: dict[str, Path] = {}
    for pattern in ("*.json.zst", "*.json"):
        for path in sorted(args.dir.glob(pattern)):
            paths.setdefault(sim_stem(path), path)

    results: dict[tuple[str, str, str], float] = {}
    for path in paths.values():
        parsed = parse_filename(path)
        if parsed is None:
            continue
        model, domain, variant = parsed
        if model not in MODELS or domain not in DOMAINS:
            continue
        results[(model, domain, variant)] = avg_reward(path)

    if not results:
        print(f"No matching simulation files found in {args.dir}/")
        return

    print("pass^1 (average reward): TACIT (scala) vs. baseline (plain)\n")
    rows = []
    for model in MODELS:
        for domain in DOMAINS:
            plain = results.get((model, domain, "plain"))
            scala = results.get((model, domain, "scala"))
            delta = (scala - plain) if plain is not None and scala is not None else None
            rows.append(
                [
                    model,
                    domain,
                    f"{plain:.3f}" if plain is not None else "—",
                    f"{scala:.3f}" if scala is not None else "—",
                    f"{delta:+.3f}" if delta is not None else "—",
                ]
            )
    print_table(["Model", "Domain", "plain", "scala", "Δ"], rows)


if __name__ == "__main__":
    main()
