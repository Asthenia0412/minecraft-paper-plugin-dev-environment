package dev.minecraft.rpg.adapter;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.CharacterId;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CharacterRegistry {
    private final Map<UUID, Character> characters = new HashMap<>();

    public Character getOrCreate(UUID playerId) {
        return characters.computeIfAbsent(playerId,
                id -> Character.create(new CharacterId(id.toString()), 100));
    }
}

