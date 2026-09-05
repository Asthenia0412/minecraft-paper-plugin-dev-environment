package dev.minecraft.rpg.combat;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.character.application.CharacterRepository;
import dev.minecraft.rpg.common.DomainEvent;
import dev.minecraft.rpg.common.EventPublisher;
import dev.minecraft.rpg.common.TraceId;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatApplicationServiceTest {
    @Test
    void publishesDeathEventWhenAttackDefeatsCharacter() {
        CharacterId id = new CharacterId("target");
        InMemoryCharacters repository = new InMemoryCharacters();
        repository.save(Character.create(id, 20));
        CapturingPublisher publisher = new CapturingPublisher();
        CombatApplicationService service = new CombatApplicationService(repository, publisher);

        CombatResult result = service.attack(new AttackCommand(id, 20, new TraceId("trace-attack")));

        assertTrue(result.defeated());
        PlayerDefeated event = assertInstanceOf(PlayerDefeated.class, publisher.event);
        assertEquals(id, event.characterId());
        assertEquals("trace-attack", event.traceId().value());
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

