package dev.minecraft.rpg.bootstrap;

import dev.minecraft.rpg.adapter.CharacterRegistry;
import dev.minecraft.rpg.adapter.RpgCommand;
import dev.minecraft.rpg.common.LocalEventBus;
import dev.minecraft.rpg.economy.RewardService;
import dev.minecraft.rpg.economy.Wallet;
import dev.minecraft.rpg.infrastructure.FileCharacterRepository;
import dev.minecraft.rpg.infrastructure.FileEquipmentRepository;
import org.bukkit.plugin.java.JavaPlugin;

public final class RpgEnginePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        LocalEventBus eventBus = new LocalEventBus();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        Wallet wallet = new Wallet(getDataFolder().toPath().resolve("wallet.properties"));
        new RewardService(eventBus, wallet);
        CharacterRegistry characters = new CharacterRegistry(
                new FileCharacterRepository(getDataFolder().toPath().resolve("characters.properties")));
        RpgCommand command = new RpgCommand(characters, eventBus, wallet,
                new dev.minecraft.rpg.adapter.EquipmentRegistry(new FileEquipmentRepository(
                        getDataFolder().toPath().resolve("equipment.properties"))));
        getCommand("rpg").setExecutor(command);
        getLogger().info("RPG Engine enabled");
        if (Boolean.getBoolean("rpg.showcase")) {
            getServer().getScheduler().runTask(this, () -> new dev.minecraft.rpg.adapter.LuoxiaWorld(this).open());
        }
    }
}
