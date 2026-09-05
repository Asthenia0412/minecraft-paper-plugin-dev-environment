package dev.minecraft.rpg.infrastructure;

import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.item.Equipment;
import dev.minecraft.rpg.item.EquipmentRepository;
import dev.minecraft.rpg.item.EquipmentSlot;
import dev.minecraft.rpg.item.ItemDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

public final class FileEquipmentRepository implements EquipmentRepository {
    private final Path file;

    public FileEquipmentRepository(Path file) {
        this.file = file;
    }

    @Override
    public Optional<Equipment> findByCharacter(CharacterId characterId) {
        Properties properties = load();
        Equipment equipment = new Equipment();
        String prefix = characterId.value() + ".";
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            String id = properties.getProperty(prefix + slot.name() + ".id");
            String attack = properties.getProperty(prefix + slot.name() + ".attack");
            if (id != null && attack != null) {
                equipment.equip(new ItemDefinition(id, slot, Integer.parseInt(attack)));
            }
        }
        return equipment.items().isEmpty() ? Optional.empty() : Optional.of(equipment);
    }

    @Override
    public void save(CharacterId characterId, Equipment equipment) {
        Properties properties = load();
        String prefix = characterId.value() + ".";
        for (ItemDefinition item : equipment.items()) {
            properties.setProperty(prefix + item.slot().name() + ".id", item.id());
            properties.setProperty(prefix + item.slot().name() + ".attack", Integer.toString(item.attack()));
        }
        try (OutputStream output = Files.newOutputStream(file)) {
            properties.store(output, "RPG equipment state");
        } catch (IOException error) {
            throw new IllegalStateException("Unable to save equipment state", error);
        }
    }

    private Properties load() {
        Properties properties = new Properties();
        if (!Files.exists(file)) return properties;
        try (InputStream input = Files.newInputStream(file)) {
            properties.load(input);
            return properties;
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load equipment state", error);
        }
    }
}

