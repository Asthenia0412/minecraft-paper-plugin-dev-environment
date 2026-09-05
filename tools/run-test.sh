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

PAPER_JAR_PATH="$("$ROOT_DIR/Server/scripts/bootstrap.sh")"
cp "$ROOT_DIR/Server/config/server.properties" "$RUNTIME/server.properties"

shopt -s nullglob
PAPER_JAR=("$PAPER_JAR_PATH")

LOG="$RUNTIME/server.log"
PID_FILE="$RUNTIME/server.pid"
cleanup() {
  if [[ -s "$PID_FILE" ]] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
    kill "$(cat "$PID_FILE")" 2>/dev/null || true
    wait "$(cat "$PID_FILE")" 2>/dev/null || true
  fi
  rm -f "$PID_FILE"
}
trap cleanup EXIT

(cd "$RUNTIME" && java -jar "$(basename "${PAPER_JAR[0]}")" --nogui) >"$LOG" 2>&1 &
echo $! > "$PID_FILE"
for _ in {1..60}; do
  if grep -qE 'Done \([^)]+\)! For help, type "help"' "$LOG"; then
    break
  fi
  if ! kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
    cat "$LOG" >&2
    exit 4
  fi
  sleep 1
done
grep -qE 'Done \([^)]+\)! For help, type "help"' "$LOG" || {
  echo "Paper did not become ready within 60 seconds" >&2
  cat "$LOG" >&2
  exit 5
}
grep -q 'ExamplePlugin enabled' "$LOG"
grep -q 'Smoke check: /devkit status -> ExamplePlugin status: OK' "$LOG"

echo "Paper integration test passed: ${PAPER_JAR[0]}"
