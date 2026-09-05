#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUNTIME="$ROOT_DIR/Server/runtime"
if [[ "${EULA_ACCEPTED:-false}" != "true" ]]; then
  echo "Set EULA_ACCEPTED=true to accept the Minecraft EULA explicitly." >&2
  exit 2
fi
mkdir -p "$RUNTIME/plugins"
cp "$ROOT_DIR/Server/config/server.properties" "$RUNTIME/server.properties"
printf 'eula=true\n' > "$RUNTIME/eula.txt"

shopt -s nullglob
PAPER_JAR=("$RUNTIME"/paper-*.jar)
if (( ${#PAPER_JAR[@]} != 1 )); then
  echo "Paper runtime is not installed. Run the bootstrap step for the locked Paper build first." >&2
  exit 3
fi

echo "Paper bootstrap is ready: ${PAPER_JAR[0]}"
