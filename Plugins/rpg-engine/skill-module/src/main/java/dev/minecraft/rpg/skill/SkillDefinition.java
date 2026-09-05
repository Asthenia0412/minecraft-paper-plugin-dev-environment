package dev.minecraft.rpg.skill;

public record SkillDefinition(String id, long cooldownSeconds, int resourceCost) {
    public SkillDefinition {
        if (id == null || id.isBlank() || cooldownSeconds < 0 || resourceCost < 0) {
            throw new IllegalArgumentException("Invalid skill definition");
        }
    }
}

