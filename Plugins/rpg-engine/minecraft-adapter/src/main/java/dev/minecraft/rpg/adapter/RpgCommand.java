package dev.minecraft.rpg.adapter;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.combat.CombatResult;
import dev.minecraft.rpg.combat.CombatService;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RpgCommand implements CommandExecutor {
    private final CharacterRegistry registry;
    private final CombatService combatService = new CombatService();

    public RpgCommand(CharacterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use RPG commands."));
            return true;
        }
        Character character = registry.getOrCreate(player.getUniqueId());
        if (args.length == 1 && args[0].equalsIgnoreCase("status")) {
            player.sendMessage(Component.text("RPG character health: " + character.health()));
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("attack")) {
            try {
                CombatResult result = combatService.attack(character, Integer.parseInt(args[1]));
                player.sendMessage(Component.text("RPG attack damage: " + result.damageApplied()
                        + ", health: " + character.health()));
                return true;
            } catch (IllegalArgumentException error) {
                player.sendMessage(Component.text("Damage must be a non-negative integer."));
                return true;
            }
        }
        player.sendMessage(Component.text("Usage: /rpg status | /rpg attack <damage>"));
        return true;
    }
}
