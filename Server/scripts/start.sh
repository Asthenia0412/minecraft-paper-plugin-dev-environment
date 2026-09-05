#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
RUNTIME="$ROOT_DIR/Server/runtime"
if [[ "${EULA_ACCEPTED:-false}" != "true" ]]; then
  echo "Set EULA_ACCEPTED=true to accept the Minecraft EULA explicitly." >&2
  exit 2
fi
mkdir -p "$RUNTIME/plugins"
cp "$ROOT_DIR/Server/config/server.properties" "$RUNTIME/server.properties"
printf 'eula=true\n' > "$RUNTIME/eula.txt"
PAPER_JAR="$("$ROOT_DIR/Server/scripts/bootstrap.sh")"

if [[ -s "$RUNTIME/server.pid" ]] && kill -0 "$(<"$RUNTIME/server.pid")" 2>/dev/null; then
  echo "Paper is already running with PID $(<"$RUNTIME/server.pid")"
  exit 0
fi

(cd "$RUNTIME" && exec java -jar "$(basename "$PAPER_JAR")" --nogui) >"$RUNTIME/server.log" 2>&1 &
echo $! > "$RUNTIME/server.pid"
for _ in {1..60}; do
  if grep -qE 'Done \([^)]+\)! For help, type "help"' "$RUNTIME/server.log"; then
    echo "Paper is ready at 127.0.0.1:25565"
    exit 0
  fi
  if ! kill -0 "$(<"$RUNTIME/server.pid")" 2>/dev/null; then
    cat "$RUNTIME/server.log" >&2
    exit 1
  fi
  sleep 1
done
echo "Paper did not become ready within 60 seconds" >&2
exit 1
