#!/usr/bin/env bash
set -Eeuo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
if [[ -x /usr/libexec/java_home ]]; then
  export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
fi
export MC_TEST_USERNAME=LuoxiaProbe
export MC_TEST_COMMAND='luoxia status'
export MC_TEST_EXPECTED='Luoxia ready: world=luoxia_peak_v1, buildings=22, spawnSafe=true, center=true'
exec "$ROOT_DIR/gradlew" -p "$ROOT_DIR/tools/headless-client" run
