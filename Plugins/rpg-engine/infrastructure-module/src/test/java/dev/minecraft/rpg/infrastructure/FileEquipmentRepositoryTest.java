package dev.minecraft.rpg.infrastructure;

import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.item.Equipment;
import dev.minecraft.rpg.item.EquipmentSlot;
import dev.minecraft.rpg.item.ItemDefinition;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileEquipmentRepositoryTest {
    @Test
    void persistsEquippedItems() throws Exception {
        var file = Files.createTempFile("rpg-equipment", ".properties");
        CharacterId id = new CharacterId("player-1");
        Equipment equipment = new Equipment();
        equipment.equip(new ItemDefinition("iron-sword", EquipmentSlot.MAIN_HAND, 7));

        new FileEquipmentRepository(file).save(id, equipment);
        Equipment restored = new FileEquipmentRepository(file).findByCharacter(id).orElseThrow();

        assertEquals(7, restored.totalAttack());
        Files.deleteIfExists(file);
    }
}

