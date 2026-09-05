package dev.minecraft.rpg.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EquipmentTest {
    @Test
    void equipsOneItemPerSlotAndReplacesThePreviousItem() {
        Equipment equipment = new Equipment();
        ItemDefinition woodenSword = new ItemDefinition("wooden-sword", EquipmentSlot.MAIN_HAND, 3);
        ItemDefinition ironSword = new ItemDefinition("iron-sword", EquipmentSlot.MAIN_HAND, 7);

        equipment.equip(woodenSword);
        equipment.equip(ironSword);

        assertEquals(ironSword, equipment.itemIn(EquipmentSlot.MAIN_HAND));
        assertEquals(7, equipment.totalAttack());
    }
}

