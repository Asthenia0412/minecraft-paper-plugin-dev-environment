package dev.minecraft.rpg.skill;

import dev.minecraft.rpg.common.TraceId;

public record CastResult(boolean success, long remainingCooldownSeconds, TraceId traceId) {
}

