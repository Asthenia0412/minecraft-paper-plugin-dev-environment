package dev.minecraft.rpg.adapter;

import dev.minecraft.rpg.item.Equipment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EquipmentRegistry {
    private final Map<UUID, Equipment> equipment = new HashMap<>();

    public Equipment forPlayer(UUID playerId) {
        return equipment.computeIfAbsent(playerId, ignored -> new Equipment());
    }
}

