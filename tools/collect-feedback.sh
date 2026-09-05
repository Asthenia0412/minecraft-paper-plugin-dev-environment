#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN_ID="${RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
OUT_DIR="$ROOT_DIR/feedback/latest"
HISTORY_DIR="$ROOT_DIR/feedback/history/$RUN_ID"
mkdir -p "$OUT_DIR" "$HISTORY_DIR"
dirty=false
git -C "$ROOT_DIR" diff --quiet || dirty=true
head_commit="$(git -C "$ROOT_DIR" rev-parse HEAD 2>/dev/null || echo unknown)"

cat > "$OUT_DIR/summary.json" <<JSON
{
  "feedback_schema_version": 1,
  "run_id": "$RUN_ID",
  "run_kind": "${RUN_KIND:-build}",
  "commit": {"head": "$head_commit", "dirty": $dirty},
  "started_at": "${STARTED_AT:-$(date -u +%Y-%m-%dT%H:%M:%SZ)}",
  "finished_at": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "status": "${RUN_STATUS:-failed}",
  "steps": [],
  "artifact": null,
  "server": null,
  "errors": ["Detailed step records are attached by the pipeline."]
}
JSON
cp "$OUT_DIR/summary.json" "$HISTORY_DIR/summary.json"
echo "Feedback written to $OUT_DIR/summary.json"
