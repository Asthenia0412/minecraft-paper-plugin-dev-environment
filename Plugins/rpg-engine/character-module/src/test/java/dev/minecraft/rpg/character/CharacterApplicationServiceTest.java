package dev.minecraft.rpg.character;

import dev.minecraft.rpg.character.application.CharacterApplicationService;
import dev.minecraft.rpg.character.application.CharacterRepository;
import dev.minecraft.rpg.character.application.CreateCharacterCommand;
import dev.minecraft.rpg.character.domain.CharacterCreated;
import dev.minecraft.rpg.common.DomainEvent;
import dev.minecraft.rpg.common.EventPublisher;
import dev.minecraft.rpg.common.TraceId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CharacterApplicationServiceTest {
    @Test
    void createsCharacterPersistsItAndPublishesDomainEvent() {
        InMemoryCharacters repository = new InMemoryCharacters();
        CapturingPublisher publisher = new CapturingPublisher();
        CharacterId id = new CharacterId("player-2");
        CharacterApplicationService service = new CharacterApplicationService(repository, publisher,
                Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC));

        Character character = service.create(new CreateCharacterCommand(id, new TraceId("trace-1")));

        assertEquals(character, repository.findById(id).orElseThrow());
        CharacterCreated event = assertInstanceOf(CharacterCreated.class, publisher.event);
        assertEquals(id, event.characterId());
        assertEquals("trace-1", event.traceId().value());
    }

    private static final class InMemoryCharacters implements CharacterRepository {
        private final Map<CharacterId, Character> values = new HashMap<>();

        @Override public Optional<Character> findById(CharacterId id) { return Optional.ofNullable(values.get(id)); }
        @Override public void save(Character character) { values.put(character.id(), character); }
    }

    private static final class CapturingPublisher implements EventPublisher {
        private DomainEvent event;
        @Override public void publish(DomainEvent event) { this.event = event; }
    }
}

