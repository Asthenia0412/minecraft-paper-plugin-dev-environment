package dev.minecraft.rpg.infrastructure;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.character.application.CharacterRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

public final class FileCharacterRepository implements CharacterRepository {
    private final Path file;

    public FileCharacterRepository(Path file) {
        this.file = file;
    }

    @Override
    public Optional<Character> findById(CharacterId id) {
        Properties properties = load();
        String prefix = id.value() + ".";
        String maxHealth = properties.getProperty(prefix + "max-health");
        String health = properties.getProperty(prefix + "health");
        if (maxHealth == null || health == null) {
            return Optional.empty();
        }
        return Optional.of(Character.restore(id, Integer.parseInt(maxHealth), Integer.parseInt(health)));
    }

    @Override
    public void save(Character character) {
        Properties properties = load();
        String prefix = character.id().value() + ".";
        properties.setProperty(prefix + "max-health", Integer.toString(character.maxHealth()));
        properties.setProperty(prefix + "health", Integer.toString(character.health()));
        try (OutputStream output = Files.newOutputStream(file)) {
            properties.store(output, "RPG character state");
        } catch (IOException error) {
            throw new IllegalStateException("Unable to save character state", error);
        }
    }

    private Properties load() {
        Properties properties = new Properties();
        if (!Files.exists(file)) {
            return properties;
        }
        try (InputStream input = Files.newInputStream(file)) {
            properties.load(input);
            return properties;
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load character state", error);
        }
    }
}

