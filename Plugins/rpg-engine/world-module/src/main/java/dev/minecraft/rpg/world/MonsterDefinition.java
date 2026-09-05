package dev.minecraft.rpg.world;

public record MonsterDefinition(WorldContentId id, int level, int health) {
    public MonsterDefinition {
        if (level <= 0 || health <= 0) throw new IllegalArgumentException("Monster stats must be positive");
    }
}

