#!/usr/bin/env bash
set -euo pipefail

TACIT_DIR="$HOME/Workspace/tacit"
DEST_DIR="$(pwd)"

(cd "$TACIT_DIR" && ./build.sh)

cp "$TACIT_DIR/TACIT.jar" "$DEST_DIR/"
cp "$TACIT_DIR/TACIT-library.jar" "$DEST_DIR/"
