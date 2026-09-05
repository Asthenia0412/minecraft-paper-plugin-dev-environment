#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CLIENT_DIR="${MINECRAFT_CLIENT_DIR:-}"
if [[ -z "$CLIENT_DIR" || ! -d "$CLIENT_DIR" ]]; then
  echo "Set MINECRAFT_CLIENT_DIR to your legally obtained Minecraft client directory." >&2
  exit 2
fi
echo "Connect the client at $(<"$ROOT_DIR/Client/config/server-address.txt")"
echo "Client automation is intentionally delegated to the installed launcher."
