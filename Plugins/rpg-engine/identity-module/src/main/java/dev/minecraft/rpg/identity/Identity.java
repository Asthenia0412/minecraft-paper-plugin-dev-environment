package dev.minecraft.rpg.identity;

import java.util.UUID;

public record Identity(UUID id, String displayName) {
    public Identity {
        if (id == null || displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Invalid identity");
        }
    }
}

