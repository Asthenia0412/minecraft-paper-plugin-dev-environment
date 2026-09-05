package dev.minecraft.rpg.adapter;

import dev.minecraft.rpg.item.Equipment;
import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.item.EquipmentRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EquipmentRegistry {
    private final Map<UUID, Equipment> equipment = new HashMap<>();
    private final EquipmentRepository persistence;

    public EquipmentRegistry() { this.persistence = null; }

    public EquipmentRegistry(EquipmentRepository persistence) { this.persistence = persistence; }

    public Equipment forPlayer(UUID playerId) {
        return equipment.computeIfAbsent(playerId, id -> persistence == null
                ? new Equipment()
                : persistence.findByCharacter(new CharacterId(id.toString())).orElseGet(Equipment::new));
    }

    public void save(UUID playerId) {
        if (persistence != null) {
            persistence.save(new CharacterId(playerId.toString()), forPlayer(playerId));
        }
    }
}
