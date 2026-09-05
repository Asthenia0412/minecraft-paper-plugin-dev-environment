package dev.minecraft.rpg.adapter;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.character.application.CharacterRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CharacterRegistry implements CharacterRepository {
    private final Map<UUID, Character> characters = new HashMap<>();

    public Character getOrCreate(UUID playerId) {
        return characters.computeIfAbsent(playerId,
                id -> Character.create(new CharacterId(id.toString()), 100));
    }

    @Override
    public java.util.Optional<Character> findById(CharacterId id) {
        return characters.values().stream().filter(character -> character.id().equals(id)).findFirst();
    }

    @Override
    public void save(Character character) {
        characters.put(UUID.fromString(character.id().value()), character);
    }
}
