# RPG Engine

This project is a modular monolith running on Paper. Minecraft is treated as
the runtime; RPG state and rules are owned by the engine.

## Boundaries

- `common`: trace IDs, domain events, and event publication contracts.
- `character-module`: character state, creation use case, and repository port.
- `combat-module`: attack command, damage rules, and defeat events.
- `skill-module`: data-driven skill definitions and owner-scoped cooldowns.
- `item-module`: item definitions and equipment aggregate.
- `economy-module`: wallet and defeat rewards.
- `quest-module`, `dungeon-module`, `world-module`: supporting bounded contexts.
- `infrastructure-module`: file-backed repository adapters.
- `minecraft-adapter`: Paper command and runtime translation layer.
- `bootstrap-plugin`: composition root producing `rpg-engine-plugin.jar`.

Core modules must not depend on Bukkit or Minecraft classes. Paper objects may
only appear in `minecraft-adapter` and `bootstrap-plugin`.

## Local Verification

```bash
EULA_ACCEPTED=true ./tools/pipeline.sh
```

The pipeline builds and tests every Gradle module, assembles one plugin JAR,
starts Paper, connects the protocol-level Headless Client, and executes:

`create -> equip -> cast -> attack -> death reward -> status`

Feedback is written to `feedback/latest/summary.json` and historical run
records are stored under `feedback/history/`.

## Extending the Engine

For each feature, identify its bounded context, Gradle module, domain model,
command/query boundary, domain event flow, and test strategy before adding the
Paper adapter. Keep game content in configuration files such as `item.yml` and
`skill.yml`, not in Minecraft-native attributes.

