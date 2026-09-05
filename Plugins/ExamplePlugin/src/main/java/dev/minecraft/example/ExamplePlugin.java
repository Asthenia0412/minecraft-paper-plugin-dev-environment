package dev.minecraft.example;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExamplePlugin extends JavaPlugin implements CommandExecutor {
    @Override
    public void onEnable() {
        getCommand("devkit").setExecutor(this);
        getLogger().info("ExamplePlugin enabled");
        getLogger().info("Smoke check: /devkit status -> ExamplePlugin status: OK");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("status")) {
            sender.sendMessage(Component.text("ExamplePlugin status: OK"));
            return true;
        }
        sender.sendMessage(Component.text("Usage: /devkit status"));
        return true;
    }
}
