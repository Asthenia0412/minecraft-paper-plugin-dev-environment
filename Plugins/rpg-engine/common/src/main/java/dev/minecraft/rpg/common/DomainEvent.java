package dev.minecraft.rpg.common;

import java.time.Instant;

public interface DomainEvent {
    TraceId traceId();

    Instant occurredAt();
}

