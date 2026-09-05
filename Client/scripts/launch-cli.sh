#!/usr/bin/env bash
set -Eeuo pipefail

CLIENT_DIR="${MINECRAFT_CLIENT_DIR:-}"
VERSION="${MINECRAFT_VERSION:-1.21.11}"
USERNAME="${MINECRAFT_USERNAME:-DevPlayer}"
ASSET_INDEX="${MINECRAFT_ASSET_INDEX:-29}"
if [[ -z "$CLIENT_DIR" ]]; then
  echo "Set MINECRAFT_CLIENT_DIR to your legally obtained Minecraft directory." >&2
  exit 2
fi
VERSION_DIR="$CLIENT_DIR/versions/$VERSION"
CLIENT_JAR="$VERSION_DIR/$VERSION.jar"
ASSETS_DIR="$CLIENT_DIR/assets"
LIBRARIES_DIR="$CLIENT_DIR/libraries"
if [[ ! -f "$CLIENT_JAR" || ! -d "$ASSETS_DIR" || ! -d "$LIBRARIES_DIR" ]]; then
  echo "Missing $VERSION client.jar, assets, or libraries in $CLIENT_DIR" >&2
  echo "Install the legally obtained $VERSION client before using this script." >&2
  exit 3
fi

CLASSPATH="$CLIENT_JAR"
while IFS= read -r -d '' jar; do CLASSPATH="$CLASSPATH:$jar"; done < <(find "$LIBRARIES_DIR" -type f -name '*.jar' -print0)
UUID="00000000-0000-0000-0000-000000000001"
JAVA_ARGS=()
if [[ "$(uname -s)" == "Darwin" ]]; then
  JAVA_ARGS+=("-XstartOnFirstThread")
fi
exec java "${JAVA_ARGS[@]}" -cp "$CLASSPATH" net.minecraft.client.main.Main \
  --username "$USERNAME" \
  --version "$VERSION" \
  --gameDir "$CLIENT_DIR" \
  --assetsDir "$ASSETS_DIR" \
  --assetIndex "$ASSET_INDEX" \
  --uuid "$UUID" \
  --accessToken offline \
  --userType legacy \
  --versionType dev \
  --quickPlayMultiplayer 127.0.0.1:25565
