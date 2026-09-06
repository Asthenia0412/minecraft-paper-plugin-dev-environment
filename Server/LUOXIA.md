# Luoxia Peak

An original Minecraft interpretation of the sunset, mountain and flowering-tree
atmosphere of Luoxia Peak, not a replica or an import of the original game's assets.

Build with `./tools/build-and-deploy.sh`, then launch with
`EULA_ACCEPTED=true ./Server/scripts/start-luoxia.sh`.
Stop an already running server with `./Server/scripts/stop.sh` first.

The opt-in showcase uses a separate world, `luoxia_peak_v1`, and leaves existing
worlds intact. Players join at `(0.5, 101, 0.5)`, facing the northern sanctuary.
Creative mode enables flight for exploring the architecture. `/luoxia` returns
to the center; `/luoxia status` reports the world, building count and spawn safety.

The layout contains a central inlaid court, a double-roof sanctuary, side halls,
residential courtyards, a ceremonial gate, a three-tier pagoda, pavilions, a lotus
lake, an arched bridge, flowering groves and a rocky mountain horizon. Twilight
is fixed and hostile spawning is disabled. No resource pack is required.

With the showcase server running, `./tools/test-luoxia.sh` connects a separate
Headless player and checks the world, building count, safe spawn blocks and
actual player position. It does not restart the server or clear plugin data.

Generation is deterministic and chunk based. The implementation lives in the
Minecraft adapter; core RPG domain modules remain independent of Bukkit.
The map is generated once per chunk and saved by Paper. Future incompatible
layout revisions should use a new world name to avoid seams in existing chunks.
