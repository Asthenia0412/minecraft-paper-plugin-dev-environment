package dev.minecraft.rpg.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class LocalEventBus implements EventPublisher {
    private final Map<Class<?>, List<Consumer<?>>> handlers = new HashMap<>();

    public <T extends DomainEvent> void subscribe(Class<T> type, Consumer<T> handler) {
        handlers.computeIfAbsent(type, ignored -> new ArrayList<>()).add(handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void publish(DomainEvent event) {
        for (Consumer<?> handler : handlers.getOrDefault(event.getClass(), List.of())) {
            ((Consumer<DomainEvent>) handler).accept(event);
        }
    }
}

