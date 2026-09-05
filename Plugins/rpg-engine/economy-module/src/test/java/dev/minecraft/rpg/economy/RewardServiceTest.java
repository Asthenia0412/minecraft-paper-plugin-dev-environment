package dev.minecraft.rpg.economy;

import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.combat.PlayerDefeated;
import dev.minecraft.rpg.common.LocalEventBus;
import dev.minecraft.rpg.common.TraceId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardServiceTest {
    @Test
    void grantsRewardWhenCharacterDefeatedEventIsPublished() {
        LocalEventBus eventBus = new LocalEventBus();
        Wallet wallet = new Wallet();
        new RewardService(eventBus, wallet);

        eventBus.publish(new PlayerDefeated(new CharacterId("player-1"), new TraceId("trace"), Instant.EPOCH));

        assertEquals(10, wallet.balance(new CharacterId("player-1")));
    }
}

