package dev.minecraft.rpg.dungeon;

import java.util.UUID;

public record DungeonInstance(UUID id, String definitionId, DungeonState state) {
    public DungeonInstance {
        if (id == null || definitionId == null || definitionId.isBlank() || state == null) {
            throw new IllegalArgumentException("Invalid dungeon instance");
        }
    }
}

