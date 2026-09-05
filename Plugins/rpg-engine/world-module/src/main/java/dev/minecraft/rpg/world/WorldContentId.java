package dev.minecraft.rpg.world;

public record WorldContentId(String value) {
    public WorldContentId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Content id must not be blank");
    }
}

