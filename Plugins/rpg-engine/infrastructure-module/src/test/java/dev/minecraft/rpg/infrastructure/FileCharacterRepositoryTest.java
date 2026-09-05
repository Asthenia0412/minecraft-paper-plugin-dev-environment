package dev.minecraft.rpg.infrastructure;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.CharacterId;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileCharacterRepositoryTest {
    @Test
    void restoresCharacterStateFromDisk() throws Exception {
        var file = Files.createTempFile("rpg-characters", ".properties");
        CharacterId id = new CharacterId("player-1");
        FileCharacterRepository first = new FileCharacterRepository(file);
        Character character = Character.create(id, 100);
        character.takeDamage(35);
        first.save(character);

        Character restored = new FileCharacterRepository(file).findById(id).orElseThrow();

        assertEquals(65, restored.health());
        Files.deleteIfExists(file);
    }
}

