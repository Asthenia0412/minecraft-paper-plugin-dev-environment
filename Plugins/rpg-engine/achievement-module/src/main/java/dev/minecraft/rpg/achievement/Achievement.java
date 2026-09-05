package dev.minecraft.rpg.achievement;

import dev.minecraft.rpg.character.CharacterId;

public final class Achievement {
    private final AchievementId id;
    private final int target;
    private int progress;

    public Achievement(AchievementId id, int target) {
        if (target <= 0) throw new IllegalArgumentException("Achievement target must be positive");
        this.id = id;
        this.target = target;
    }

    public void record(CharacterId characterId) {
        if (characterId == null) throw new IllegalArgumentException("Character is required");
        progress = Math.min(target, progress + 1);
    }

    public boolean unlocked() { return progress >= target; }
    public AchievementId id() { return id; }
}

