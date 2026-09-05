#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PLUGIN_DIR="$ROOT_DIR/Plugins/ExamplePlugin"
DEPLOY_DIR="$ROOT_DIR/Server/runtime/plugins"

mkdir -p "$DEPLOY_DIR"
"$ROOT_DIR/gradlew" -p "$PLUGIN_DIR" test jar

shopt -s nullglob
artifacts=("$PLUGIN_DIR"/build/libs/ExamplePlugin-*.jar)
if (( ${#artifacts[@]} != 1 )); then
  echo "Expected exactly one plugin JAR, found ${#artifacts[@]}" >&2
  exit 1
fi

rm -f "$DEPLOY_DIR"/ExamplePlugin-*.jar
cp "${artifacts[0]}" "$DEPLOY_DIR/"
sha256 -q "${artifacts[0]}" 2>/dev/null || shasum -a 256 "${artifacts[0]}"
echo "Deployed ${artifacts[0]} to $DEPLOY_DIR"
