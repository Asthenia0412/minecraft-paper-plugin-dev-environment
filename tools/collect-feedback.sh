#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN_ID="${RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
RUN_KIND="${RUN_KIND:-build}"
OUT_DIR="$ROOT_DIR/feedback/latest"
HISTORY_DIR="$ROOT_DIR/feedback/history/$RUN_ID"
mkdir -p "$OUT_DIR" "$HISTORY_DIR"
dirty=false
git -C "$ROOT_DIR" diff --quiet || dirty=true
head_commit="$(git -C "$ROOT_DIR" rev-parse HEAD 2>/dev/null || echo unknown)"
status="${RUN_STATUS:-failed}"
step_status="$status"
if [[ "$RUN_KIND" == "integration" && "$status" == "failed" ]]; then
  step_status=failed
fi
if [[ "${RUN_KIND:-build}" == "integration" ]]; then
  steps_json="$(jq -cn --arg s "$step_status" '["build","unit_test","deploy","server_readiness","plugin_enable","smoke_check","cleanup"] | map({name: ., status: $s, started_at: null, finished_at: null, exit_code: (if $s == "passed" then 0 else 1 end)})')"
else
  steps_json="$(jq -cn --arg s "$step_status" '["build","unit_test","deploy"] | map({name: ., status: $s, started_at: null, finished_at: null, exit_code: (if $s == "passed" then 0 else 1 end)})')"
fi
artifact_json=null
artifact_path="$ROOT_DIR/Server/runtime/plugins/ExamplePlugin-0.1.0.jar"
if [[ -f "$artifact_path" ]]; then
  artifact_json="$(jq -cn --arg path "$artifact_path" --arg sha "$(shasum -a 256 "$artifact_path" | awk '{print $1}')" '{path: $path, sha256: $sha}')"
fi
server_json=null
if [[ -f "$ROOT_DIR/Server/runtime/server.log" ]]; then
  ready=false
  grep -qE 'Done \([^)]+\)! For help, type "help"' "$ROOT_DIR/Server/runtime/server.log" && ready=true
  server_json="$(jq -cn --arg log "$ROOT_DIR/Server/runtime/server.log" --argjson ready "$ready" '{log: $log, ready: $ready, exit_code: 0}')"
fi

cat > "$OUT_DIR/summary.json" <<JSON
{
  "feedback_schema_version": 1,
  "run_id": "$RUN_ID",
  "run_kind": "${RUN_KIND:-build}",
  "commit": {"head": "$head_commit", "dirty": $dirty},
  "started_at": "${STARTED_AT:-$(date -u +%Y-%m-%dT%H:%M:%SZ)}",
  "finished_at": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "status": "${RUN_STATUS:-failed}",
  "steps": $steps_json,
  "artifact": $artifact_json,
  "server": $server_json,
  "errors": ["Detailed step records are attached by the pipeline."]
}
JSON
cp "$OUT_DIR/summary.json" "$HISTORY_DIR/summary.json"
echo "Feedback written to $OUT_DIR/summary.json"
