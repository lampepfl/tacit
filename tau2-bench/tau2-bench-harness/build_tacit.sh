#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
TACIT_DIR="$SCRIPT_DIR/../tacit"
DEST_JAR="$SCRIPT_DIR/SafeExecMCP.jar"

if [[ ! -f "$TACIT_DIR/build.sbt" ]]; then
  echo "TACIT submodule not initialized at $TACIT_DIR" >&2
  echo "Run from the repository root: git submodule update --init --recursive" >&2
  exit 1
fi

if ! command -v sbt >/dev/null 2>&1; then
  echo "Error: sbt is not available on PATH." >&2
  exit 1
fi

echo "Running sbt assembly in $TACIT_DIR ..."
SBT_OUTPUT="$(cd "$TACIT_DIR" && sbt -batch -no-colors "assembly" "show assembly / assemblyOutputPath" 2>&1)"

JAR_PATH="$(printf '%s\n' "$SBT_OUTPUT" | grep -Eo '/[^ ]*/target/scala-[^ /]*/[^ /]*assembly[^ /]*\.jar' | tail -n1 || true)"

if [[ -z "$JAR_PATH" || ! -f "$JAR_PATH" ]]; then
  echo "Failed to locate assembled jar in sbt output." >&2
  echo "$SBT_OUTPUT" >&2
  exit 1
fi

cp -f "$JAR_PATH" "$DEST_JAR"
echo "SafeExecMCP jar copied to:"
echo "  $DEST_JAR"
