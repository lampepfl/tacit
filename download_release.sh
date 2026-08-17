#!/usr/bin/env bash
set -euo pipefail

# Download two precompiled JARs from the latest GitHub release and verify
# their checksums against the release metadata.
# Usage:
#   ./download_release.sh [--pre-release] [dist-path]
#
# The JARs are downloaded to a temporary directory and moved into dist-path
# only after both checksums verify, so a failed or tampered download never
# replaces a previously installed JAR.

OWNER_REPO="lampepfl/tacit"

fail() {
  echo "Error: $*" >&2
  exit 1
}

# Fetch a URL to stdout with HTTP status handling (403 rate-limit hint).
fetch_release_json() {
  local api_url="$1"

  if command -v curl >/dev/null 2>&1; then
    local curl_args=(-sSL -H "Accept: application/vnd.github+json")
    local response http_code body

    if [[ -n "${GITHUB_TOKEN:-}" ]]; then
      curl_args+=(-H "Authorization: Bearer ${GITHUB_TOKEN}")
    fi

    response="$(curl "${curl_args[@]}" -w $'\n%{http_code}' "$api_url")" \
      || fail "failed to query the GitHub API at ${api_url}."
    http_code="${response##*$'\n'}"
    body="${response%$'\n'*}"

    case "$http_code" in
      2*) ;;
      403)
        fail "GitHub API returned 403 Forbidden for ${api_url}. You may be rate limited; set GITHUB_TOKEN to raise the rate limit."
        ;;
      *)
        fail "GitHub API returned HTTP ${http_code} for ${api_url}."
        ;;
    esac

    printf '%s\n' "$body"
  elif command -v wget >/dev/null 2>&1; then
    local wget_args=(--header="Accept: application/vnd.github+json" -q -O -)

    if [[ -n "${GITHUB_TOKEN:-}" ]]; then
      wget_args+=(--header="Authorization: Bearer ${GITHUB_TOKEN}")
    fi

    wget "${wget_args[@]}" "$api_url" \
      || fail "failed to query the GitHub API at ${api_url}. If you are rate limited, set GITHUB_TOKEN to raise the rate limit."
  else
    fail "curl or wget is required but neither was found."
  fi
}

# Download a URL to a file.
http_download() {
  local url="$1" out="$2"

  if command -v curl >/dev/null 2>&1; then
    curl -fL "$url" -o "$out"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$out" "$url"
  else
    fail "curl or wget is required but neither was found."
  fi
}

# Print "name<TAB>url<TAB>digest" lines for the TACIT jar assets.
# Prefer jq when available: it parses JSON structurally and does not depend
# on field order within asset objects. Fall back to the grep state machine,
# which pairs name -> digest -> browser_download_url by token order and stops
# at the second release object (a "tag_name" token) so that, when the input is
# the releases array, both jars come from the same, newest release.
extract_assets() {
  local json="$1"

  if command -v jq >/dev/null 2>&1; then
    extract_assets_jq "$json"
  else
    extract_assets_grep "$json"
  fi
}

extract_assets_jq() {
  local json="$1"
  local parsed

  if ! parsed="$(printf '%s' "$json" | jq -r '
    [ .assets[]?
      | select(.name == "TACIT.jar" or .name == "TACIT-library.jar")
      | "\(.name)\t\(.browser_download_url)\t\(.digest // "")"
    ] | unique | .[]
  ' 2>/dev/null)"; then
    fail "failed to parse the release metadata with jq."
  fi

  printf '%s\n' "$parsed" | grep -qF $'TACIT.jar\t' \
    || fail "required asset TACIT.jar not found in the release metadata."
  printf '%s\n' "$parsed" | grep -qF $'TACIT-library.jar\t' \
    || fail "required asset TACIT-library.jar not found in the release metadata."

  printf '%s\n' "$parsed"
}

extract_assets_grep() {
  local json="$1"
  local library_url="" library_digest=""
  local assembly_url="" assembly_digest=""
  local current_name="" current_digest=""
  local url="" releases_seen=0

  while IFS= read -r token; do
    # Each release object carries exactly one top-level "tag_name" (asset and
    # user objects have none); it precedes the release's assets. Stop before
    # the second release so assets are never paired across releases.
    if [[ "$token" =~ \"tag_name\"[[:space:]]*: ]]; then
      releases_seen=$((releases_seen + 1))
      [[ "$releases_seen" -le 1 ]] || break
      continue
    fi

    if [[ "$token" =~ \"name\"[[:space:]]*:[[:space:]]*\"([^\"]+)\" ]]; then
      current_name="${BASH_REMATCH[1]}"
      continue
    fi

    if [[ "$token" =~ \"digest\"[[:space:]]*:[[:space:]]*\"([^\"]+)\" ]]; then
      current_digest="${BASH_REMATCH[1]}"
      continue
    fi

    if [[ "$token" =~ \"browser_download_url\"[[:space:]]*:[[:space:]]*\"([^\"]+)\" ]]; then
      url="${BASH_REMATCH[1]}"

      if [[ -z "$library_url" && "$current_name" == "TACIT-library.jar" ]]; then
        library_url="$url"
        library_digest="$current_digest"
      fi

      if [[ -z "$assembly_url" && "$current_name" == "TACIT.jar" ]]; then
        assembly_url="$url"
        assembly_digest="$current_digest"
      fi

      current_name=""
      current_digest=""
    fi
  done < <(printf '%s' "$json" | grep -oE '"tag_name"[[:space:]]*:|"name"[[:space:]]*:[[:space:]]*"[^"]+"|"digest"[[:space:]]*:[[:space:]]*"[^"]+"|"browser_download_url"[[:space:]]*:[[:space:]]*"[^"]+"')

  [[ -n "$assembly_url" ]] || fail "required asset TACIT.jar not found in the release metadata."
  [[ -n "$library_url" ]] || fail "required asset TACIT-library.jar not found in the release metadata."

  printf 'TACIT.jar\t%s\t%s\nTACIT-library.jar\t%s\t%s\n' \
    "$assembly_url" "$assembly_digest" \
    "$library_url" "$library_digest"
}

sha256_of() {
  if command -v sha256sum >/dev/null 2>&1; then
    sha256sum "$1" | awk '{print $1}'
  elif command -v shasum >/dev/null 2>&1; then
    shasum -a 256 "$1" | awk '{print $1}'
  else
    return 1
  fi
}

verify_jar() {
  local file="$1" digest="$2"
  local name expected actual

  name="$(basename "$file")"

  # Fail closed: a matched TACIT asset without a digest is exactly the
  # tamper/corruption case we must not install.
  if [[ -z "$digest" ]]; then
    fail "no digest in release metadata for ${name}; refusing to install an unverified jar."
  fi

  # digest format is "sha256:<64 hex chars>"
  if [[ "$digest" =~ ^sha256:([0-9a-fA-F]{64})$ ]]; then
    expected="${BASH_REMATCH[1]}"
  else
    fail "unrecognised digest format '${digest}' for ${name}; expected 'sha256:' followed by 64 hex characters."
  fi

  if ! actual="$(sha256_of "$file")"; then
    fail "cannot verify ${name}: no sha256 tool found. Install coreutils (sha256sum) or use macOS shasum."
  fi

  if [[ "$actual" != "$expected" ]]; then
    fail "Checksum mismatch for ${name}: expected ${expected}, got ${actual}"
  fi
  echo "  ${name}: verified (sha256)"
}

usage() {
  echo "Usage: $0 [--pre-release] [dist-path]"
  echo "  --pre-release  download the newest release even if it is a pre-release"
  echo "  dist-path      directory to install TACIT.jar and TACIT-library.jar into (default: .)"
}

main() {
  local pre_release=false
  local dest_dir="."
  local dest_given=false

  local arg
  for arg in "$@"; do
    case "$arg" in
      --pre-release) pre_release=true ;;
      -h|--help) usage; return 0 ;;
      -*) usage >&2; fail "unknown option '${arg}'." ;;
      *)
        if [[ "$dest_given" == true ]]; then
          usage >&2
          fail "unexpected extra argument '${arg}'."
        fi
        dest_dir="$arg"
        dest_given=true
        ;;
    esac
  done

  local api_url
  if [[ "$pre_release" == true ]]; then
    api_url="https://api.github.com/repos/${OWNER_REPO}/releases"
    echo "Fetching latest pre-release metadata for ${OWNER_REPO}..."
  else
    api_url="https://api.github.com/repos/${OWNER_REPO}/releases/latest"
    echo "Fetching latest release metadata for ${OWNER_REPO}..."
  fi

  local release_json
  release_json="$(fetch_release_json "$api_url")"

  if [[ "$pre_release" == true ]] && command -v jq >/dev/null 2>&1; then
    # The releases endpoint returns an array, newest first; keep the newest
    # release object so asset pairing cannot span releases. (The grep fallback
    # stops at the second release object for the same reason.)
    release_json="$(printf '%s' "$release_json" | jq -c '.[0]' 2>/dev/null)" \
      || fail "failed to parse the pre-release metadata."
    if [[ -z "$release_json" || "$release_json" == "null" ]]; then
      fail "no releases found for ${OWNER_REPO}."
    fi
  fi

  local assets_info
  assets_info="$(extract_assets "$release_json")"

  mkdir -p "$dest_dir"

  # Download into a temporary directory and only move verified jars into
  # place, so a failed download or checksum never clobbers an installed jar.
  local tmp_dir
  tmp_dir="$(mktemp -d "${dest_dir}/.tacit-download.XXXXXX")"
  trap "rm -rf '$tmp_dir'" EXIT

  local name url digest
  echo "Downloading to ${dest_dir}"
  while IFS=$'\t' read -r name url digest; do
    [[ -n "$name" && -n "$url" ]] || continue
    echo "- ${name}"
    http_download "$url" "${tmp_dir}/${name}" \
      || fail "failed to download ${name} from ${url}."
  done <<< "$assets_info"

  echo "Verifying checksums..."
  while IFS=$'\t' read -r name url digest; do
    [[ -n "$name" ]] || continue
    [[ -f "${tmp_dir}/${name}" ]] || fail "${name} was not downloaded."
    verify_jar "${tmp_dir}/${name}" "$digest"
  done <<< "$assets_info"

  mv "${tmp_dir}/TACIT.jar" "${dest_dir}/TACIT.jar"
  mv "${tmp_dir}/TACIT-library.jar" "${dest_dir}/TACIT-library.jar"
  trap - EXIT
  rmdir "$tmp_dir" 2>/dev/null || true

  echo "Done. Downloaded the latest TACIT JARs:"
  ls -1 "$dest_dir"/*.jar
}

# Run main only when executed directly, so the test suite can source the
# functions. `${BASH_SOURCE[0]:-$0}`: BASH_SOURCE is unset under `set -u` when
# the script is piped into bash (bash 3.2), which must still run main.
if [[ "${BASH_SOURCE[0]:-$0}" == "$0" ]]; then
  main "$@"
fi
