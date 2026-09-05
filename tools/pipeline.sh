#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN_ID="${RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
STARTED_AT="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
status=failed

finish() {
  RUN_KIND=integration RUN_STATUS="$status" RUN_ID="$RUN_ID" STARTED_AT="$STARTED_AT" \
    "$ROOT_DIR/tools/collect-feedback.sh" || true
}
trap finish EXIT

RUN_ID="$RUN_ID" "$ROOT_DIR/tools/build-and-deploy.sh"
EULA_ACCEPTED="${EULA_ACCEPTED:-false}" "$ROOT_DIR/tools/run-test.sh"
status=passed
