package dev.minecraft.rpg.character;

public final class Character {
    private final CharacterId id;
    private final int maxHealth;
    private int health;

    private Character(CharacterId id, int maxHealth) {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be positive");
        }
        this.id = id;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public static Character create(CharacterId id, int maxHealth) {
        return new Character(id, maxHealth);
    }

    public static Character restore(CharacterId id, int maxHealth, int health) {
        Character character = new Character(id, maxHealth);
        if (health < 0 || health > maxHealth) {
            throw new IllegalArgumentException("Health outside character bounds");
        }
        character.health = health;
        return character;
    }

    public int maxHealth() {
        return maxHealth;
    }

    public CharacterId id() {
        return id;
    }

    public int health() {
        return health;
    }

    public DamageResult takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage must not be negative");
        }
        int applied = Math.min(amount, health);
        health -= applied;
        return new DamageResult(applied, health == 0);
    }
}
