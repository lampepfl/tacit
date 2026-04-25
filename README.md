# Artifact for "Securing Agents With Tracked Capabilities"

This document guides the reproduction of the results in the paper.

## TACIT: implementation and test suite

The directory `tacit/` contains the reference implementation of **TACIT** (*Tracked Agent Capabilities In Types*) together with the test suite used in the paper. TACIT is a safety harness that sits between an LLM agent and the outside world: agents submit Scala 3 code, which is type-checked with [capture checking](https://nightly.scala-lang.org/docs/reference/experimental/capture-checking/index.html) and executed against a capability-safe library; the harness is exposed to agents as an [MCP](https://modelcontextprotocol.io/) server.

All commands below are run from `tacit/`.

### Prerequisites

- Linux or macOS
- A JDK (Java 17+)
- [`sbt`](https://www.scala-sbt.org/download.html) 1.12+: Scala build tool used to build and test TACIT

### Building TACIT

The `build.sh` script runs `sbt assembly` and copies the two resulting JARs into the current directory:

```bash
./build.sh
```

This produces:

- `TACIT.jar` — the MCP server
- `TACIT-library.jar` — the capability-safe library loaded by the server

Other parts of this artifact (the safety benchmark, SWE-bench harness, AgentDojo scripts) expect these JARs to be available at this location.

### Running the test suite

The test suite covers the capture-checking enforcement rules, the capability library, and the MCP server. Run it with:

```bash
sbt test
```

To run a single suite, use the usual sbt syntax, e.g. `sbt "testOnly *McpServerSuite"`.

### Further documentation

See `tacit/README.md` for the full TACIT documentation: the capability API reference, instructions for launching the MCP server directly, guidance on extending the library with new capabilities, and the results of the expressiveness evaluation.

## Reproducing Safety benchmark

The directory `safety/` contains a security benchmark, built on [AgentDojo](https://github.com/ethz-spylab/agentdojo), that evaluates whether an LLM agent using TACIT can be tricked into leaking classified data via prompt injection, direct requests, or social engineering.
All commands below are run from `safety/`.

### Prerequisites

- TACIT must be built first (see [Building TACIT](#building-tacit))
- Python 3.10+ with [`agentdojo`](https://pypi.org/project/agentdojo/) installed (`pip install agentdojo`)
- A JVM (Java 17+)
- An API key for one of the openai-compatible providers.

### Setup

Create `bench_config.json` from the example and fill in your API key and model:

```bash
cp bench_config.example.json bench_config.json
```

The config file should look like this:

```json
{
  "mcpCommand": "java -jar ../tacit/TACIT.jar --library-jar ../tacit/TACIT-library.jar",
  "llm": {
    "baseUrl": "...",
    "apiKey": "sk-or-...",
    "model": "..."
  }
}
```

`mcpCommand` points at the TACIT JARs built earlier. The LLM provider must be openai compatible (e.g. OpenAI, OpenRouter, Anthropic) and specified in the `baseUrl` and `model` fields.

### Running the experiments

Each run evaluates the agent on a task under two modes: **classified** (secrets wrapped in `Classified[T]`, enforced by TACIT) and **unclassified** (baseline, secrets as plain text). The `--classified-mode` flag selects `classified`, `unclassified`, or `both` (default).

Full benchmark — all (user task × injection task) pairs plus all malicious tasks, run in both modes:

```bash
python run_benchmark.py --benchmark
```

Other common invocations:

```bash
# Run all regular user tasks (no injections)
python run_benchmark.py --all

# Run only the malicious-prompt tasks
python run_benchmark.py --malicious

# Run a single task
python run_benchmark.py --task user_task_1
```

Execution logs for each run are written to `log/<run_id>/<task_id>_<mode>/`, and a summary of utility and security pass counts is printed at the end.

See `safety/README.md` for the full task catalog the complete CLI reference, and details of the pipeline and injection mechanism.

## Reproducing SWE-bench Lite

The directory `swebench/` contains the harness for running [SWE-bench Lite](https://www.swebench.com/) (300 real GitHub issue-fix tasks) against the [opencode](https://opencode.ai/) agent, optionally constrained to the TACIT MCP server.
All commands below are run from `swebench/`.

### Prerequisites

- TACIT must be built first (see [Building TACIT](#building-tacit))
- Docker (required for the evaluation step)
- [`opencode`](https://opencode.ai/) v1.2.5+ installed at `~/.opencode/bin/opencode` (or set `OPENCODE_BIN`)
- Python 3.10+ with [`swebench`](https://pypi.org/project/swebench/) and [`datasets`](https://pypi.org/project/datasets/) (`pip install swebench datasets`)
- An API key for the LLM provider used by opencode (configured via `opencode`; by default the runner uses an OpenRouter model — see `run_swebench.py`)

### Running the experiments

The runner supports two modes, selected with `--mode`:

| Mode | Tools available to the agent |
|------|-------------------------------|
| `default` (baseline) | opencode's built-in tools (bash, edit, read, grep, ...); no MCP |
| `mcp_only` | Only the TACIT `scala-exec` MCP tool; all built-in tools denied |

Run the full benchmark (all 300 instances) in TACIT-only mode:

```bash
python run_swebench.py --mode mcp_only
```

Other common invocations:

```bash
# Baseline: opencode with built-in tools only
python run_swebench.py

# Single instance
python run_swebench.py --instance django__django-11039

# First N instances
python run_swebench.py --first 10

# Parallel workers
python run_swebench.py -j 4

# Resume the most recent run, skipping completed instances
python run_swebench.py --resume
```

Each run writes predictions to `swebench_runs/<timestamp>/predictions.jsonl` along with per-instance logs and cloned repos under `workspace/`.

### Evaluating the predictions

The official SWE-bench harness runs each predicted patch inside a per-instance Docker container and reports the resolved rate. Make sure Docker is running, then:

```bash
python eval_swebench.py swebench_runs/<timestamp>/predictions.jsonl
```

Results are written to `evaluation_results/<run_id>/`, and a pass/fail summary with the resolved rate is printed at the end. On Apple Silicon you may need `--namespace ''` (the default) to force local image builds.

See `swebench/README.md` for additional options.

## Reproducing tau2-bench

The directory `tau2-bench/` contains the τ²-bench harness for reproducing our results on the airline and retail customer-support domains.
All commands below are run from `tau2-bench/tau2-bench-harness/`.

### Prerequisites

- [`uv`](https://docs.astral.sh/uv/getting-started/installation/): Python environment and runner
- A JVM (Java 17+)
- [`sbt`](https://www.scala-sbt.org/download.html) 1.12+: Scala build tool, used to build TACIT
- [`zstd`](https://github.com/facebook/zstd): needed to decompress the pre-computed simulation traces
- An `OPENROUTER_API_KEY` environment variable (all scripts route through OpenRouter)

### Pre-run data

Pre-computed simulation traces (zstd-compressed) are included under `data/simulations/`. To render the results table from them:

```bash
uv run util_scripts/render_results.py
```

The script prints a pass^1 comparison of the plain tool-calling baseline against the Scala/TACIT variant, per (model, domain). It accepts an alternate directory as an argument.

### Running the experiments yourself

The TACIT source lives in `tau2-bench/tacit`, which is a git submodule. If you haven't already, fetch it from the repository root:

```bash
git submodule update --init --recursive
```

Then build the TACIT MCP server, which the Scala-mode scripts load at runtime:

```bash
bash build_tacit.sh
```

This produces `SafeExecMCP.jar` in the harness directory.

Then run the experiments. Each experiment is a shell script of the form `run_{model}_{domain}[_scala].sh`, where:

- `{model}` is one of `deepseek-v3.2`, `gpt-oss-120b`, `minimax-m2.5`
- `{domain}` is one of `airline`, `retail`
- The `_scala` suffix selects the TACIT (Scala/MCP) variant; without it the agent uses native tool calls.

For example, to run the airline domain with DeepSeek V3.2 under TACIT:

```bash
bash run_deepseek-v3.2_airline_scala.sh
```

Each script writes a trace to `data/simulations/{model}_{domain}[_scala].json`. After running them, re-run the render script above to regenerate the table.

## Reproducing AgentDojo (stock version)

The directory `agentdojo-stock` contains code and data for reproducing our results for agentdojo. 
All commands below are run from `agentdojo-stock/agentdojo/`.

### Prerequisites

- [`uv`](https://docs.astral.sh/uv/getting-started/installation/): Python environment and runner
- A JVM (Java 17+)
- [`sbt`](https://www.scala-sbt.org/download.html): Scala build tool, used to build TACIT

### Pre-run data

Pre-computed run outputs (produced on our machine) are available in `runs/`. To render the results table from them:

```bash
uv run util_scripts/render_run_results.py runs/
```

### Running the experiments yourself

The TACIT source lives in `agentdojo-stock/tacit`, which is a git submodule. If you haven't already, fetch it from the repository root:

```bash
git submodule update --init --recursive
```

Then build the TACIT JARs (`TACIT.jar` and `TACIT-library.jar`), which the experiment scripts load at runtime:

```bash
bash build_tacit.sh
```

Then run the experiments. Each experiment is a shell script of the form `run_{domain}_{model}.sh`, where:

- `{domain}` is one of `banking`, `slack`, `travel`, `workspace`
- `{model}` is one of `gemini-2.5-pro`, `o4-mini-high`

For example, to run the banking suite with Gemini 2.5 Pro:

```bash
bash run_banking_gemini-2.5-pro.sh
```

After running the scripts, pass the resulting `runs/` directory to `render_run_results.py` as shown above to regenerate the table.

