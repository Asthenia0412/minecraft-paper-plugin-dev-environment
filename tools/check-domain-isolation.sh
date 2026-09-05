#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENGINE_DIR="$ROOT_DIR/Plugins/rpg-engine"
CORE_DIRS=(common character-module combat-module skill-module item-module economy-module quest-module dungeon-module world-module infrastructure-module)

for module in "${CORE_DIRS[@]}"; do
  if rg -n '(^|\.)((org\.bukkit)|(net\.minecraft))\.' "$ENGINE_DIR/$module/src"; then
    echo "Domain isolation violation in $module" >&2
    exit 1
  fi
done

echo "Domain isolation check passed"

