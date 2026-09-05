package dev.minecraft.rpg.item;

public record ItemDefinition(String id, EquipmentSlot slot, int attack) {
    public ItemDefinition {
        if (id == null || id.isBlank() || slot == null || attack < 0) {
            throw new IllegalArgumentException("Invalid item definition");
        }
    }
}

