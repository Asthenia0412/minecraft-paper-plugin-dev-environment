package dev.minecraft.rpg.character;

public record CharacterId(String value) {
    public CharacterId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Character id must not be blank");
        }
    }
}
