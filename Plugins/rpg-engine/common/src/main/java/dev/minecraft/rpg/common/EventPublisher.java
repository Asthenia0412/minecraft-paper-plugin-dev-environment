package dev.minecraft.rpg.common;

public interface EventPublisher {
    void publish(DomainEvent event);
}

