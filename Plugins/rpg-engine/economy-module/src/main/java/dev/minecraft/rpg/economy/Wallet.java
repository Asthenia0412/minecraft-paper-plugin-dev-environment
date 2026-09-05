package dev.minecraft.rpg.economy;

import dev.minecraft.rpg.character.CharacterId;

import java.util.HashMap;
import java.util.Map;

public final class Wallet {
    private final Map<CharacterId, Integer> balances = new HashMap<>();

    public void deposit(CharacterId characterId, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
        balances.merge(characterId, amount, Integer::sum);
    }

    public int balance(CharacterId characterId) {
        return balances.getOrDefault(characterId, 0);
    }
}

