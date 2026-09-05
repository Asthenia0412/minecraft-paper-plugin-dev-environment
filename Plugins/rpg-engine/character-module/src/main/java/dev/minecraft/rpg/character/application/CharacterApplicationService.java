package dev.minecraft.rpg.character.application;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.common.EventPublisher;
import dev.minecraft.rpg.character.domain.CharacterCreated;

import java.time.Clock;

public final class CharacterApplicationService {
    private final CharacterRepository repository;
    private final EventPublisher eventPublisher;
    private final Clock clock;

    public CharacterApplicationService(CharacterRepository repository, EventPublisher eventPublisher, Clock clock) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    public Character create(CreateCharacterCommand command) {
        Character character = Character.create(command.characterId(), 100);
        repository.save(character);
        eventPublisher.publish(new CharacterCreated(command.characterId(), command.traceId(), clock.instant()));
        return character;
    }
}

