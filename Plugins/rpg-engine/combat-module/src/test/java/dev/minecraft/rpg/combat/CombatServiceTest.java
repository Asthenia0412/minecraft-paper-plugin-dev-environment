package dev.minecraft.rpg.combat;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.CharacterId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatServiceTest {
    @Test
    void appliesDamageAndReportsDefeat() {
        Character target = Character.create(new CharacterId("target"), 20);

        CombatResult result = new CombatService().attack(target, 25);

        assertEquals(0, target.health());
        assertTrue(result.defeated());
    }
}

