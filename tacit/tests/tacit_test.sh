#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Source the script under test (guarded main does not execute)
source "$REPO_ROOT/tacit"

FIXTURE_JSON="$(cat "$SCRIPT_DIR/fixtures/release.json")"

# ---------------------------------------------------------------------------
# Test helpers
# ---------------------------------------------------------------------------

pass_count=0
fail_count=0

assert_eq() {
  local label="$1" expected="$2" actual="$3"
  if [[ "$expected" == "$actual" ]]; then
    echo "  PASS: $label"
    ((pass_count++)) || true
  else
    echo "  FAIL: $label"
    echo "    expected: $(printf '%q' "$expected")"
    echo "    actual:   $(printf '%q' "$actual")"
    ((fail_count++)) || true
  fi
}

assert_contains() {
  local label="$1" needle="$2" haystack="$3"
  if [[ "$haystack" == *"$needle"* ]]; then
    echo "  PASS: $label"
    ((pass_count++)) || true
  else
    echo "  FAIL: $label"
    echo "    expected to contain: $needle"
    echo "    in: $haystack"
    ((fail_count++)) || true
  fi
}

assert_not_contains() {
  local label="$1" needle="$2" haystack="$3"
  if [[ "$haystack" != *"$needle"* ]]; then
    echo "  PASS: $label"
    ((pass_count++)) || true
  else
    echo "  FAIL: $label"
    echo "    expected NOT to contain: $needle"
    echo "    in: $haystack"
    ((fail_count++)) || true
  fi
}

# Run a command in a subshell and capture exit code (for testing fail paths)
assert_fails() {
  local label="$1"
  shift
  local rc=0
  ("$@") >/dev/null 2>&1 || rc=$?
  if [[ "$rc" -ne 0 ]]; then
    echo "  PASS: $label (exit $rc)"
    ((pass_count++)) || true
  else
    echo "  FAIL: $label (expected non-zero exit, got 0)"
    ((fail_count++)) || true
  fi
}

assert_succeeds() {
  local label="$1"
  shift
  local rc=0
  ("$@") >/dev/null 2>&1 || rc=$?
  if [[ "$rc" -eq 0 ]]; then
    echo "  PASS: $label"
    ((pass_count++)) || true
  else
    echo "  FAIL: $label (expected exit 0, got $rc)"
    ((fail_count++)) || true
  fi
}

# ---------------------------------------------------------------------------
# extract_release_field
# ---------------------------------------------------------------------------

echo "--- extract_release_field ---"

assert_eq "extracts tag_name" \
  "v0.2.0" \
  "$(extract_release_field "$FIXTURE_JSON" "tag_name")"

assert_eq "extracts name" \
  "v0.2.0" \
  "$(extract_release_field "$FIXTURE_JSON" "name")"

assert_eq "missing field returns empty" \
  "" \
  "$(extract_release_field "$FIXTURE_JSON" "nonexistent_field")"

# ---------------------------------------------------------------------------
# extract_release_id
# ---------------------------------------------------------------------------

echo "--- extract_release_id ---"

assert_eq "extracts top-level id" \
  "12345678" \
  "$(extract_release_id "$FIXTURE_JSON")"

assert_eq "empty json returns empty" \
  "" \
  "$(extract_release_id "{}")"

# ---------------------------------------------------------------------------
# extract_assets
# ---------------------------------------------------------------------------

echo "--- extract_assets ---"

assets_output="$(extract_assets "$FIXTURE_JSON")"

assert_contains "output has TACIT.jar URL" \
  "TACIT.jar" \
  "$assets_output"

assert_contains "output has TACIT-library.jar URL" \
  "TACIT-library.jar" \
  "$assets_output"

assert_contains "TACIT.jar download URL" \
  "https://github.com/lampepfl/tacit/releases/download/v0.2.0/TACIT.jar" \
  "$assets_output"

assert_contains "TACIT-library.jar download URL" \
  "https://github.com/lampepfl/tacit/releases/download/v0.2.0/TACIT-library.jar" \
  "$assets_output"

assert_contains "TACIT.jar digest" \
  "sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890" \
  "$assets_output"

assert_contains "TACIT-library.jar digest" \
  "sha256:fedcba0987654321fedcba0987654321fedcba0987654321fedcba0987654321" \
  "$assets_output"

assert_not_contains "extra assets ignored" \
  "checksums.txt" \
  "$assets_output"

# Missing TACIT.jar
json_no_server='{"assets":[{"name":"TACIT-library.jar","browser_download_url":"https://x/TACIT-library.jar"}]}'
assert_fails "fails when TACIT.jar missing" extract_assets "$json_no_server"

# Missing TACIT-library.jar
json_no_lib='{"assets":[{"name":"TACIT.jar","browser_download_url":"https://x/TACIT.jar"}]}'
assert_fails "fails when TACIT-library.jar missing" extract_assets "$json_no_lib"

# No digest (older releases)
json_no_digest='{"assets":[{"name":"TACIT.jar","browser_download_url":"https://x/TACIT.jar"},{"name":"TACIT-library.jar","browser_download_url":"https://x/TACIT-library.jar"}]}'
assets_no_digest="$(extract_assets "$json_no_digest")"
assert_contains "works without digest" "TACIT.jar" "$assets_no_digest"

# ---------------------------------------------------------------------------
# release_key_from_json
# ---------------------------------------------------------------------------

echo "--- release_key_from_json ---"

assert_eq "builds release key" \
  "12345678|v0.2.0" \
  "$(release_key_from_json "$FIXTURE_JSON")"

assert_fails "fails on empty json" release_key_from_json '{}'

# ---------------------------------------------------------------------------
# java_major_version (mocked)
# ---------------------------------------------------------------------------

echo "--- java_major_version ---"

MOCK_BIN="$(mktemp -d)"
trap 'rm -rf "$MOCK_BIN"' EXIT

make_mock_java() {
  local version_string="$1"
  cat > "$MOCK_BIN/java" <<MOCK
#!/usr/bin/env bash
if [[ "\$1" == "-version" ]]; then
  echo '$version_string' >&2
  exit 0
fi
MOCK
  chmod +x "$MOCK_BIN/java"
}

# Modern JDK format
make_mock_java 'openjdk version "21.0.1" 2023-10-17'
assert_eq "parses modern JDK 21" "21" "$(PATH="$MOCK_BIN:$PATH" java_major_version)"

make_mock_java 'openjdk version "17" 2021-09-14'
assert_eq "parses JDK 17 short" "17" "$(PATH="$MOCK_BIN:$PATH" java_major_version)"

make_mock_java 'openjdk version "17.0.2" 2022-01-18'
assert_eq "parses JDK 17.0.2" "17" "$(PATH="$MOCK_BIN:$PATH" java_major_version)"

# Legacy 1.x format
make_mock_java 'java version "1.8.0_292"'
assert_eq "parses legacy 1.8 as 8" "8" "$(PATH="$MOCK_BIN:$PATH" java_major_version)"

# Unparseable
make_mock_java 'something weird'
assert_fails "fails on unparseable output" bash -c "export PATH='$MOCK_BIN:\$PATH'; source '$REPO_ROOT/tacit'; java_major_version"

# ---------------------------------------------------------------------------
# profile_candidates
# ---------------------------------------------------------------------------

echo "--- profile_candidates ---"

zsh_profiles="$(SHELL=/bin/zsh profile_candidates)"
assert_contains "zsh includes .zshrc" ".zshrc" "$zsh_profiles"
assert_contains "zsh includes .zprofile" ".zprofile" "$zsh_profiles"

bash_profiles="$(SHELL=/bin/bash profile_candidates)"
assert_contains "bash includes .bashrc" ".bashrc" "$bash_profiles"
assert_contains "bash includes .bash_profile" ".bash_profile" "$bash_profiles"
assert_contains "bash includes .profile" ".profile" "$bash_profiles"

other_profiles="$(SHELL=/bin/fish profile_candidates)"
assert_contains "other shell includes .profile" ".profile" "$other_profiles"

empty_profiles="$(SHELL= profile_candidates)"
assert_contains "empty SHELL includes .profile" ".profile" "$empty_profiles"

# ---------------------------------------------------------------------------
# path_snippet
# ---------------------------------------------------------------------------

echo "--- path_snippet ---"

snippet="$(path_snippet)"
assert_contains "has begin marker" "$PATH_MARKER_BEGIN" "$snippet"
assert_contains "has end marker" "$PATH_MARKER_END" "$snippet"
assert_contains "has PATH export" 'export PATH=' "$snippet"
assert_contains "references .local/bin" '.local/bin' "$snippet"

# ---------------------------------------------------------------------------
# cached_release_matches
# ---------------------------------------------------------------------------

echo "--- cached_release_matches ---"

(
  TEST_TMP="$(mktemp -d)"
  trap 'rm -rf "$TEST_TMP"' EXIT

  CACHE_DIR="$TEST_TMP/cache"
  SERVER_JAR="$CACHE_DIR/TACIT.jar"
  LIBRARY_JAR="$CACHE_DIR/TACIT-library.jar"
  RELEASE_MARKER="$CACHE_DIR/release.txt"

  # All files present, matching key
  mkdir -p "$CACHE_DIR"
  touch "$SERVER_JAR" "$LIBRARY_JAR"
  echo "12345|v1.0" > "$RELEASE_MARKER"

  assert_succeeds "matches when all present and key matches" \
    cached_release_matches "12345|v1.0"

  assert_fails "fails when key differs" \
    cached_release_matches "99999|v2.0"

  # Missing server jar
  rm "$SERVER_JAR"
  assert_fails "fails when server jar missing" \
    cached_release_matches "12345|v1.0"

  # Missing library jar
  touch "$SERVER_JAR"
  rm "$LIBRARY_JAR"
  assert_fails "fails when library jar missing" \
    cached_release_matches "12345|v1.0"

  # Missing release marker
  touch "$LIBRARY_JAR"
  rm "$RELEASE_MARKER"
  assert_fails "fails when release marker missing" \
    cached_release_matches "12345|v1.0"
)

# ---------------------------------------------------------------------------
# remove_profile_path
# ---------------------------------------------------------------------------

echo "--- remove_profile_path ---"

(
  TEST_HOME="$(mktemp -d)"
  trap 'rm -rf "$TEST_HOME"' EXIT
  HOME="$TEST_HOME"
  SHELL=/bin/bash

  # Create a profile with the PATH snippet embedded
  profile="$TEST_HOME/.bashrc"
  cat > "$profile" <<PROF
# existing config
alias ll='ls -la'

# >>> tacit path >>>
case ":\$PATH:" in
  *":\$HOME/.local/bin:"*) ;;
  *) export PATH="\$HOME/.local/bin:\$PATH" ;;
esac
# <<< tacit path <<<

# more config
export EDITOR=vim
PROF

  remove_profile_path

  content="$(cat "$profile")"
  assert_not_contains "snippet removed from profile" "tacit path" "$content"
  assert_contains "existing config preserved" "alias ll" "$content"
  assert_contains "trailing config preserved" "EDITOR=vim" "$content"
)

(
  TEST_HOME="$(mktemp -d)"
  trap 'rm -rf "$TEST_HOME"' EXIT
  HOME="$TEST_HOME"
  SHELL=/bin/bash

  # Profile without markers
  profile="$TEST_HOME/.bashrc"
  echo "# just some config" > "$profile"

  before="$(cat "$profile")"
  remove_profile_path >/dev/null 2>&1
  after="$(cat "$profile")"

  assert_eq "profile without markers unchanged" "$before" "$after"
)

# ---------------------------------------------------------------------------
# verify_jar
# ---------------------------------------------------------------------------

echo "--- verify_jar ---"

(
  TEST_TMP="$(mktemp -d)"
  trap 'rm -rf "$TEST_TMP"' EXIT

  # Create a test file and compute its real sha256
  echo "test content" > "$TEST_TMP/test.jar"
  real_hash="$(sha256_of "$TEST_TMP/test.jar")"

  # Correct digest
  assert_succeeds "passes with correct digest" \
    verify_jar "$TEST_TMP/test.jar" "sha256:${real_hash}"

  # Wrong digest
  assert_fails "fails with wrong digest" \
    verify_jar "$TEST_TMP/test.jar" "sha256:0000000000000000000000000000000000000000000000000000000000000000"

  # Empty digest (older releases without digest)
  assert_succeeds "passes with empty digest" \
    verify_jar "$TEST_TMP/test.jar" ""

  # Unknown digest format
  output="$(verify_jar "$TEST_TMP/test.jar" "md5:abc123" 2>&1)"
  assert_contains "skips unrecognised digest format" "unrecognised" "$output"
)

# ---------------------------------------------------------------------------
# main dispatch
# ---------------------------------------------------------------------------

echo "--- main dispatch ---"

help_output="$(main help 2>&1)"
assert_contains "help shows Usage" "Usage:" "$help_output"

help_output2="$(main --help 2>&1)"
assert_contains "--help shows Usage" "Usage:" "$help_output2"

help_output3="$(main -h 2>&1)"
assert_contains "-h shows Usage" "Usage:" "$help_output3"

assert_fails "unknown command fails" main nosuchcmd
assert_fails "setup with extra args fails" main setup extra
assert_fails "update with extra args fails" main update extra
assert_fails "self update with extra args fails" main self update extra
assert_fails "bare uninstall gives migration message" main uninstall

self_help="$(main self help 2>&1)"
assert_contains "self help shows usage" "Usage:" "$self_help"

# ---------------------------------------------------------------------------
# Results
# ---------------------------------------------------------------------------

echo ""
echo "============================="
echo "Results: $pass_count passed, $fail_count failed"
echo "============================="
[[ "$fail_count" -eq 0 ]]
