package dev.minecraft.rpg.adapter;

import dev.minecraft.rpg.character.Character;
import dev.minecraft.rpg.character.application.CharacterApplicationService;
import dev.minecraft.rpg.character.application.CreateCharacterCommand;
import dev.minecraft.rpg.combat.CombatResult;
import dev.minecraft.rpg.combat.AttackCommand;
import dev.minecraft.rpg.combat.CombatApplicationService;
import dev.minecraft.rpg.common.TraceId;
import dev.minecraft.rpg.common.EventPublisher;
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
    private final CombatApplicationService combatService;
    private final SkillService skillService = new SkillService(Clock.systemUTC());
    private final SkillDefinition fireball = new SkillDefinition("fireball", 30, 5);
    private final EquipmentRegistry equipmentRegistry = new EquipmentRegistry();
    private final EventPublisher events;
    private final dev.minecraft.rpg.economy.Wallet wallet;

    public RpgCommand(CharacterRegistry registry) {
        this(registry, event -> { }, new dev.minecraft.rpg.economy.Wallet());
    }

    public RpgCommand(CharacterRegistry registry, EventPublisher events,
                      dev.minecraft.rpg.economy.Wallet wallet) {
        this.registry = registry;
        this.events = events;
        this.wallet = wallet;
        this.characterService = new CharacterApplicationService(registry, events, Clock.systemUTC());
        this.combatService = new CombatApplicationService(registry, events);
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
                    + ", attack: " + equipmentRegistry.forPlayer(player.getUniqueId()).totalAttack()
                    + ", coins: " + wallet.balance(character.id())));
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
                CombatResult result = combatService.attack(new AttackCommand(
                        character.id(), Integer.parseInt(args[1]), TraceId.create()));
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
            CastResult result = skillService.cast(player.getUniqueId().toString(), fireball, TraceId.create());
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
