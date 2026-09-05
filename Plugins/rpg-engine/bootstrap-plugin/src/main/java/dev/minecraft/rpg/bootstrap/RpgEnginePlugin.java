package dev.minecraft.rpg.bootstrap;

import dev.minecraft.rpg.adapter.CharacterRegistry;
import dev.minecraft.rpg.adapter.RpgCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class RpgEnginePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("rpg").setExecutor(new RpgCommand(new CharacterRegistry()));
        getLogger().info("RPG Engine enabled");
    }
}

