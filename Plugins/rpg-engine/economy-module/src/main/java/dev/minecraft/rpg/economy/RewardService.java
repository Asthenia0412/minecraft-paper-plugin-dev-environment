package dev.minecraft.rpg.economy;

import dev.minecraft.rpg.combat.PlayerDefeated;
import dev.minecraft.rpg.common.LocalEventBus;

public final class RewardService {
    private static final int DEFEAT_REWARD = 10;
    private final Wallet wallet;

    public RewardService(LocalEventBus eventBus, Wallet wallet) {
        this.wallet = wallet;
        eventBus.subscribe(PlayerDefeated.class, this::onPlayerDefeated);
    }

    private void onPlayerDefeated(PlayerDefeated event) {
        wallet.deposit(event.characterId(), DEFEAT_REWARD);
    }
}

