package dev.minecraft.rpg.common;

import java.util.UUID;

public record TraceId(String value) {
    public static TraceId create() {
        return new TraceId(UUID.randomUUID().toString());
    }
}

