package dev.minecraft.rpg.combat;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.DamageResult;

public final class CombatService {
    public CombatResult attack(Character target, int damage) {
        DamageResult result = target.takeDamage(damage);
        return new CombatResult(result.damageApplied(), result.defeated());
    }
}

