package dev.minecraft.rpg.combat;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.application.CharacterRepository;
import dev.minecraft.rpg.common.EventPublisher;

import java.time.Clock;

public final class CombatApplicationService {
    private final CharacterRepository characters;
    private final EventPublisher events;
    private final Clock clock;

    public CombatApplicationService(CharacterRepository characters, EventPublisher events) {
        this(characters, events, Clock.systemUTC());
    }

    public CombatApplicationService(CharacterRepository characters, EventPublisher events, Clock clock) {
        this.characters = characters;
        this.events = events;
        this.clock = clock;
    }

    public CombatResult attack(AttackCommand command) {
        Character target = characters.findById(command.targetId())
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + command.targetId().value()));
        CombatResult result = new CombatService().attack(target, command.damage());
        characters.save(target);
        if (result.defeated()) {
            events.publish(new PlayerDefeated(target.id(), command.traceId(), clock.instant()));
        }
        return result;
    }
}

