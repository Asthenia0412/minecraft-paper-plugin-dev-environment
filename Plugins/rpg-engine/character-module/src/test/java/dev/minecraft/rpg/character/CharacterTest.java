package dev.minecraft.rpg.character;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CharacterTest {
    @Test
    void takesDamageWithoutGoingBelowZero() {
        Character character = Character.create(new CharacterId("player-1"), 100);

        DamageResult result = character.takeDamage(125);

        assertEquals(0, character.health());
        assertTrue(result.defeated());
    }
}

