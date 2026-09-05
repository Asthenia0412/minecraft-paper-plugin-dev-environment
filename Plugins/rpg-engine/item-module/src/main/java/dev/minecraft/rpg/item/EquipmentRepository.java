package dev.minecraft.rpg.item;

import dev.minecraft.rpg.character.CharacterId;

import java.util.Optional;

public interface EquipmentRepository {
    Optional<Equipment> findByCharacter(CharacterId characterId);

    void save(CharacterId characterId, Equipment equipment);
}

