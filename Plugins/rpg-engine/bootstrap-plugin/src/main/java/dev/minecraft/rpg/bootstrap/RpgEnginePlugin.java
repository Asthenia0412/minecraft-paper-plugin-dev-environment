package dev.minecraft.rpg.bootstrap;

import dev.minecraft.rpg.adapter.CharacterRegistry;
import dev.minecraft.rpg.adapter.RpgCommand;
import dev.minecraft.rpg.common.LocalEventBus;
import dev.minecraft.rpg.economy.RewardService;
import dev.minecraft.rpg.economy.Wallet;
import dev.minecraft.rpg.infrastructure.FileCharacterRepository;
import org.bukkit.plugin.java.JavaPlugin;

public final class RpgEnginePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        LocalEventBus eventBus = new LocalEventBus();
        Wallet wallet = new Wallet();
        new RewardService(eventBus, wallet);
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        CharacterRegistry characters = new CharacterRegistry(
                new FileCharacterRepository(getDataFolder().toPath().resolve("characters.properties")));
        getCommand("rpg").setExecutor(new RpgCommand(characters, eventBus, wallet));
        getLogger().info("RPG Engine enabled");
    }
}
