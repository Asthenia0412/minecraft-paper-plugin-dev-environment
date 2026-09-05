package dev.minecraft.rpg.quest;

public record QuestId(String value) {
    public QuestId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Quest id must not be blank");
    }
}

