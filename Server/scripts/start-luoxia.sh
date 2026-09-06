#!/usr/bin/env bash
set -Eeuo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Drpg.showcase=true -Dterminal.jline=false -Dterminal.ansi=false"
exec "$ROOT_DIR/Server/scripts/start.sh"
