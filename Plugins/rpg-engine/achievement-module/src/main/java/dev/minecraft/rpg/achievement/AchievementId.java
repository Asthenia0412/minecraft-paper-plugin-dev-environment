package dev.minecraft.rpg.achievement;

public record AchievementId(String value) {
    public AchievementId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Achievement id must not be blank");
    }
}

