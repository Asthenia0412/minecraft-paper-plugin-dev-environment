package dev.minecraft.rpg.character.application;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.CharacterId;

import java.util.Optional;

public interface CharacterRepository {
    Optional<Character> findById(CharacterId id);

    void save(Character character);
}

