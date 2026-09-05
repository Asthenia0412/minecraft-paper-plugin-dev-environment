package dev.minecraft.rpg.item;

import java.util.EnumMap;
import java.util.Map;

public final class Equipment {
    private final Map<EquipmentSlot, ItemDefinition> items = new EnumMap<>(EquipmentSlot.class);

    public void equip(ItemDefinition item) {
        items.put(item.slot(), item);
    }

    public ItemDefinition itemIn(EquipmentSlot slot) {
        return items.get(slot);
    }

    public int totalAttack() {
        return items.values().stream().mapToInt(ItemDefinition::attack).sum();
    }

    public java.util.Collection<ItemDefinition> items() {
        return java.util.List.copyOf(items.values());
    }
}
