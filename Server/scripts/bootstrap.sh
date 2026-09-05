#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
RUNTIME="$ROOT_DIR/Server/runtime"
LOCK="$ROOT_DIR/Server/paper.lock"
mkdir -p "$RUNTIME"

source "$LOCK"
TARGET="$RUNTIME/$filename"
if [[ ! -f "$TARGET" ]]; then
  curl --fail --location --retry 3 \
    -H 'User-Agent: minecraft-paper-plugin-dev-environment/0.1 (https://github.com/Asthenia0412/minecraft-paper-plugin-dev-environment)' \
    "$url" -o "$TARGET"
fi

actual="$(shasum -a 256 "$TARGET" | awk '{print $1}')"
if [[ "$actual" != "$sha256" ]]; then
  echo "Paper checksum mismatch: expected $sha256, got $actual" >&2
  exit 1
fi
echo "$TARGET"
