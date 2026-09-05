package dev.minecraft.rpg.economy;

import dev.minecraft.rpg.character.CharacterId;
import dev.minecraft.rpg.combat.PlayerDefeated;
import dev.minecraft.rpg.common.LocalEventBus;
import dev.minecraft.rpg.common.TraceId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardServiceTest {
    @Test
    void restoresWalletBalanceFromDisk() throws Exception {
        var file = Files.createTempFile("rpg-wallet", ".properties");
        CharacterId id = new CharacterId("player-2");
        Wallet first = new Wallet(file);
        first.deposit(id, 25);

        assertEquals(25, new Wallet(file).balance(id));
        Files.deleteIfExists(file);
    }

    @Test
    void grantsRewardWhenCharacterDefeatedEventIsPublished() {
        LocalEventBus eventBus = new LocalEventBus();
        Wallet wallet = new Wallet();
        new RewardService(eventBus, wallet);

        eventBus.publish(new PlayerDefeated(new CharacterId("player-1"), new TraceId("trace"), Instant.EPOCH));

        assertEquals(10, wallet.balance(new CharacterId("player-1")));
    }
}
