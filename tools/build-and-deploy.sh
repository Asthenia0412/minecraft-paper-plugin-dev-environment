#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PLUGIN_DIR="$ROOT_DIR/Plugins/rpg-engine"
DEPLOY_DIR="$ROOT_DIR/Server/runtime/plugins"
RUN_ID="${RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
STARTED_AT="${STARTED_AT:-$(date -u +%Y-%m-%dT%H:%M:%SZ)}"
RUN_STATUS=failed

if command -v /usr/libexec/java_home >/dev/null 2>&1 && java -version 2>&1 | head -1 | grep -q 'version "25'; then
  if command -v /usr/libexec/java_home >/dev/null 2>&1; then
    JAVA_HOME="$(/usr/libexec/java_home -v 21)"
    export JAVA_HOME
  fi
fi

finish() {
  RUN_KIND=build RUN_STATUS="$RUN_STATUS" RUN_ID="$RUN_ID" STARTED_AT="$STARTED_AT" \
    "$ROOT_DIR/tools/collect-feedback.sh" || true
}
trap finish EXIT

mkdir -p "$DEPLOY_DIR"
"$ROOT_DIR/gradlew" -p "$PLUGIN_DIR" test :bootstrap-plugin:jar

shopt -s nullglob
artifacts=("$PLUGIN_DIR"/bootstrap-plugin/build/libs/rpg-engine-plugin-*.jar)
if (( ${#artifacts[@]} != 1 )); then
  echo "Expected exactly one plugin JAR, found ${#artifacts[@]}" >&2
  exit 1
fi

rm -f "$DEPLOY_DIR"/rpg-engine-plugin-*.jar "$DEPLOY_DIR"/ExamplePlugin-*.jar
cp "${artifacts[0]}" "$DEPLOY_DIR/"
sha256 -q "${artifacts[0]}" 2>/dev/null || shasum -a 256 "${artifacts[0]}"
RUN_STATUS=passed
echo "Deployed ${artifacts[0]} to $DEPLOY_DIR"
