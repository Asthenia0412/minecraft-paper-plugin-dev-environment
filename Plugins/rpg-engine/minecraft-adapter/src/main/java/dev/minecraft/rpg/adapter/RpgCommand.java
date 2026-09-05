package dev.minecraft.rpg.adapter;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.application.CharacterApplicationService;
import dev.minecraft.rpg.character.application.CreateCharacterCommand;
import dev.minecraft.rpg.combat.CombatResult;
import dev.minecraft.rpg.combat.CombatService;
import dev.minecraft.rpg.common.EventPublisher;
import dev.minecraft.rpg.common.TraceId;
import dev.minecraft.rpg.skill.CastResult;
import dev.minecraft.rpg.skill.SkillDefinition;
import dev.minecraft.rpg.skill.SkillService;
import dev.minecraft.rpg.item.EquipmentSlot;
import dev.minecraft.rpg.item.ItemDefinition;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Clock;

public final class RpgCommand implements CommandExecutor {
    private final CharacterRegistry registry;
    private final CharacterApplicationService characterService;
    private final CombatService combatService = new CombatService();
    private final SkillService skillService = new SkillService(Clock.systemUTC());
    private final SkillDefinition fireball = new SkillDefinition("fireball", 30, 5);
    private final EquipmentRegistry equipmentRegistry = new EquipmentRegistry();

    public RpgCommand(CharacterRegistry registry) {
        this.registry = registry;
        this.characterService = new CharacterApplicationService(registry, event -> { }, Clock.systemUTC());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use RPG commands."));
            return true;
        }
        Character character = registry.getOrCreate(player.getUniqueId());
        if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
            character = characterService.create(new CreateCharacterCommand(
                    new dev.minecraft.rpg.character.CharacterId(player.getUniqueId().toString()), TraceId.create()));
            player.sendMessage(Component.text("RPG character created: " + character.health()));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("status")) {
            player.sendMessage(Component.text("RPG character health: " + character.health()
                    + ", attack: " + equipmentRegistry.forPlayer(player.getUniqueId()).totalAttack()));
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("equip")
                && args[1].equalsIgnoreCase("iron-sword")) {
            equipmentRegistry.forPlayer(player.getUniqueId()).equip(
                    new ItemDefinition("iron-sword", EquipmentSlot.MAIN_HAND, 7));
            player.sendMessage(Component.text("RPG equipped: iron-sword, attack: 7"));
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
        if (args.length == 2 && args[0].equalsIgnoreCase("cast")
                && args[1].equalsIgnoreCase("fireball")) {
            CastResult result = skillService.cast(fireball, TraceId.create());
            if (result.success()) {
                player.sendMessage(Component.text("RPG skill cast: fireball"));
            } else {
                player.sendMessage(Component.text("RPG skill cooldown: " + result.remainingCooldownSeconds()));
            }
            return true;
        }
        player.sendMessage(Component.text("Usage: /rpg create | /rpg status | /rpg attack <damage> | /rpg cast fireball"));
        return true;
    }
}
