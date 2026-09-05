package dev.minecraft.rpg.achievement;

import dev.minecraft.rpg.character.CharacterId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AchievementTest {
    @Test
    void unlocksAchievementOnceProgressReachesTarget() {
        Achievement achievement = new Achievement(new AchievementId("first-blood"), 1);

        assertFalse(achievement.unlocked());
        achievement.record(new CharacterId("player-1"));

        assertTrue(achievement.unlocked());
    }
}

