package dev.minecraft.rpg.combat;

import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.common.DomainEvent;
import dev.minecraft.rpg.common.TraceId;

import java.time.Instant;

public record PlayerDefeated(CharacterId characterId, TraceId traceId, Instant occurredAt)
        implements DomainEvent {
}

