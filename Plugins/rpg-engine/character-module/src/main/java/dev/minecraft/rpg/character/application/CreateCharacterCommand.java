package dev.minecraft.rpg.character.application;

import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.common.TraceId;

public record CreateCharacterCommand(CharacterId characterId, TraceId traceId) {
}

