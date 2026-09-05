package dev.minecraft.rpg.combat;

import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.common.TraceId;

public record AttackCommand(CharacterId targetId, int damage, TraceId traceId) {
    public AttackCommand {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must not be negative");
        }
    }
}

