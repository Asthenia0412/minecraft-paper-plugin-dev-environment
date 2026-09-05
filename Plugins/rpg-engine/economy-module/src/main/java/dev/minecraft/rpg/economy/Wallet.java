package dev.minecraft.rpg.economy;

import dev.minecraft.rpg.character.CharacterId;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class Wallet {
    private final Map<CharacterId, Integer> balances = new HashMap<>();
    private final Path file;

    public Wallet() {
        this.file = null;
    }

    public Wallet(Path file) {
        this.file = file;
        load();
    }

    public void deposit(CharacterId characterId, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
        balances.merge(characterId, amount, Integer::sum);
        save();
    }

    public int balance(CharacterId characterId) {
        return balances.getOrDefault(characterId, 0);
    }

    private void load() {
        if (file == null || !Files.exists(file)) {
            return;
        }
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(file)) {
            properties.load(input);
            for (String key : properties.stringPropertyNames()) {
                balances.put(new CharacterId(key), Integer.parseInt(properties.getProperty(key)));
            }
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load wallet", error);
        }
    }

    private void save() {
        if (file == null) {
            return;
        }
        Properties properties = new Properties();
        balances.forEach((id, balance) -> properties.setProperty(id.value(), balance.toString()));
        try (OutputStream output = Files.newOutputStream(file)) {
            properties.store(output, "RPG wallet balances");
        } catch (IOException error) {
            throw new IllegalStateException("Unable to save wallet", error);
        }
    }
}
