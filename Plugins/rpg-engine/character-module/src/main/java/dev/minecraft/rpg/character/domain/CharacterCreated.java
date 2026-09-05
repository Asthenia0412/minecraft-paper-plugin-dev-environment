package dev.minecraft.rpg.character.domain;

import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.common.DomainEvent;
import dev.minecraft.rpg.common.TraceId;

import java.time.Instant;

public record CharacterCreated(CharacterId characterId, TraceId traceId, Instant occurredAt)
        implements DomainEvent {
}

