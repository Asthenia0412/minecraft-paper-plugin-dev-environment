package dev.minecraft.rpg.quest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestTest {
    @Test
    void capsProgressAtObjectiveTarget() {
        Quest quest = new Quest(new QuestId("first-kill"), 1);

        quest.advance(3);

        assertTrue(quest.completed());
    }
}

