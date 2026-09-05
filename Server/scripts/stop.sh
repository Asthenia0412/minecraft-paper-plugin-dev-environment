#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PID_FILE="$ROOT_DIR/Server/runtime/server.pid"
if [[ ! -s "$PID_FILE" ]]; then
  echo "Paper is not running"
  exit 0
fi
pid="$(<"$PID_FILE")"
if kill -0 "$pid" 2>/dev/null; then
  kill "$pid"
  for _ in {1..15}; do
    kill -0 "$pid" 2>/dev/null || break
    sleep 1
  done
fi
if kill -0 "$pid" 2>/dev/null; then
  echo "Paper did not stop within 15 seconds" >&2
  exit 1
fi
rm -f "$PID_FILE"
echo "Paper stopped"
