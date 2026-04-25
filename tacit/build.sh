#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   ./build.sh [--verbose|-v] [dist-path]

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
DEST_DIR="$PWD"
VERBOSE=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    -v|--verbose)
      VERBOSE=true
      shift
      ;;
    -h|--help)
      echo "Usage: $0 [--verbose|-v] [dist-path]"
      exit 0
      ;;
    -*)
      echo "Unknown option: $1" >&2
      echo "Usage: $0 [--verbose|-v] [dist-path]" >&2
      exit 1
      ;;
    *)
      if [[ "$DEST_DIR" != "$PWD" ]]; then
        echo "Usage: $0 [--verbose|-v] [dist-path]" >&2
        exit 1
      fi
      DEST_DIR="$1"
      shift
      ;;
  esac
done

if [[ "$DEST_DIR" != /* ]]; then
  DEST_DIR="$PWD/$DEST_DIR"
fi

mkdir -p "$DEST_DIR"

cd "$SCRIPT_DIR"

if ! command -v sbt >/dev/null 2>&1; then
  echo "Error: sbt is not available on PATH."
  exit 1
fi

echo "Running assembly tasks..."

SBT_ARGS=(-batch -no-colors "lib/assembly" "assembly" "show lib / assembly / assemblyOutputPath" "show assembly / assemblyOutputPath")

if [[ "$VERBOSE" == true ]]; then
  SBT_OUTPUT="$(sbt "${SBT_ARGS[@]}" 2>&1 | tee /dev/stderr)"
else
  SBT_OUTPUT="$(sbt "${SBT_ARGS[@]}" 2>&1)"
fi

LIB_JAR="$(printf '%s\n' "$SBT_OUTPUT" | grep -Eo '/[^ ]*TACIT-library\.jar' | tail -n1 || true)"
ROOT_JAR="$(printf '%s\n' "$SBT_OUTPUT" | grep -Eo '/[^ ]*/target/scala-[^ /]*/[^ /]*assembly[^ /]*\.jar' | tail -n1 || true)"

if [[ -z "$LIB_JAR" || ! -f "$LIB_JAR" ]]; then
  echo "Failed to locate library jar path in sbt output."
  echo "$SBT_OUTPUT"
  exit 1
fi

if [[ -z "$ROOT_JAR" || ! -f "$ROOT_JAR" ]]; then
  echo "Failed to locate MCP server jar path in sbt output."
  echo "$SBT_OUTPUT"
  exit 1
fi

cp -f "$LIB_JAR" "$DEST_DIR/"
cp -f "$ROOT_JAR" "$DEST_DIR/TACIT.jar"

echo "TACIT JARs built and copied to:"
echo "- $DEST_DIR/TACIT.jar"
echo "- $DEST_DIR/TACIT-library.jar"