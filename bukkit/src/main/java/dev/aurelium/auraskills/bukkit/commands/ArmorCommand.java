package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.modifier.Modifiers;
import dev.aurelium.auraskills.bukkit.modifier.Multipliers;
import dev.aurelium.auraskills.bukkit.requirement.Requirements;
import dev.aurelium.auraskills.bukkit.stat.StatFormat;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.modifier.Multiplier;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;

@CommandAlias("%skills_alias")
@Subcommand("armor")
public class ArmorCommand extends BaseCommand {

    private final AuraSkills plugin;
    private final StatFormat format = new StatFormat();

    public ArmorCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("modifier add")
    @CommandCompletion("@stats @nothing false|true")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Adds an armor stat modifier to the item held, along with lore by default.")
    public void onArmorModifierAdd(@Flags("itemheld") Player player, Stat stat, int value, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        Modifiers modifiers = new Modifiers(plugin);
        for (StatModifier statModifier : modifiers.getModifiers(ModifierType.ARMOR, item)) {
            if (statModifier.stat() == stat) {
                player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_ADD_ALREADY_EXISTS, locale), stat, locale));
                return;
            }
        }
        if (lore) {
            modifiers.addLore(ModifierType.ARMOR, item, stat, value, locale);
        }
        ItemStack newItem = modifiers.addModifier(ModifierType.ARMOR, item, stat, value);
        player.getInventory().setItemInMainHand(newItem);
        player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_ADD_ADDED, locale), stat, value, locale));

    }

    @Subcommand("modifier remove")
    @CommandCompletion("@stats false|true")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Removes an armor stat modifier from the item held, and the lore associated with it by default.")
    public void onArmorModifierRemove(@Flags("itemheld") Player player, Stat stat, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean removed = false;
        Modifiers modifiers = new Modifiers(plugin);
        for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, item)) {
            if (modifier.stat() == stat) {
                item = modifiers.removeModifier(ModifierType.ARMOR, item, stat);
                removed = true;
                break;
            }
        }
        if (lore) {
            modifiers.removeLore(item, stat, locale);
        }
        player.getInventory().setItemInMainHand(item);
        if (removed) {
            player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_REMOVE_REMOVED, locale), stat, locale));
        }
        else {
            player.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_REMOVE_DOES_NOT_EXIST, locale), stat, locale));
        }
    }

    @Subcommand("modifier list")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Lists all armor stat modifiers on the item held.")
    public void onArmorModifierList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_LIST_HEADER, locale));
        Modifiers modifiers = new Modifiers(plugin);
        for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, item)) {
            message.append("\n").append(format.applyPlaceholders(plugin.getMsg(CommandMessage.ARMOR_MODIFIER_LIST_ENTRY, locale), modifier, locale));
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("modifier removeall")
    @CommandPermission("auraskills.command.armor.modifier")
    @Description("Removes all armor stat modifiers from the item held.")
    public void onArmorModifierRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        Modifiers modifiers = new Modifiers(plugin);
        ItemStack item = modifiers.removeAllModifiers(ModifierType.ARMOR, player.getInventory().getItemInMainHand());
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ARMOR_MODIFIER_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("requirement add")
    @CommandPermission("auraskills.command.armor.requirement")
    @CommandCompletion("@skills @nothing false|true")
    @Description("Adds an armor requirement to the item held, along with lore by default")
    public void onArmorRequirementAdd(@Flags("itemheld") Player player, Skill skill, int level, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        Requirements requirements = new Requirements(plugin);
        if (requirements.hasRequirement(ModifierType.ARMOR, item, skill)) {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_ADD_ALREADY_EXISTS, locale),
                    "{skill}", skill.getDisplayName(locale)));
            return;
        }
        item = requirements.addRequirement(ModifierType.ARMOR, item, skill, level);
        if (lore) {
            requirements.addLore(ModifierType.ARMOR, item, skill, level, locale);
        }
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_ADD_ADDED, locale),
                "{skill}", skill.getDisplayName(locale),
                "{level}", String.valueOf(level)));
    }

    @Subcommand("requirement remove")
    @CommandPermission("auraskills.command.armor.requirement")
    @CommandCompletion("@skills false|true")
    @Description("Removes an armor requirement from the item held, along with the lore associated it by default.")
    public void onArmorRequirementRemove(@Flags("itemheld") Player player, Skill skill, @Default("true") boolean lore) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        Requirements requirements = new Requirements(plugin);
        if (requirements.hasRequirement(ModifierType.ARMOR, item, skill)) {
            item = requirements.removeRequirement(ModifierType.ARMOR, item, skill);
            if (lore) {
                requirements.removeLore(item, skill);
            }
            player.getInventory().setItemInMainHand(item);
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_REMOVE_REMOVED, locale),
                    "{skill}", skill.getDisplayName(locale)));
        }
        else {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_REMOVE_DOES_NOT_EXIST, locale),
                    "{skill}", skill.getDisplayName(locale)));
        }
    }

    @Subcommand("requirement list")
    @CommandPermission("auraskills.command.armor.requirement")
    @Description("Lists the armor requirements on the item held.")
    public void onArmorRequirementList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        player.sendMessage(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_LIST_HEADER, locale));
        Requirements requirements = new Requirements(plugin);
        for (Map.Entry<Skill, Integer> entry : requirements.getRequirements(ModifierType.ARMOR, player.getInventory().getItemInMainHand()).entrySet()) {
            player.sendMessage(TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_LIST_ENTRY, locale),
                    "{skill}", entry.getKey().getDisplayName(locale),
                    "{level}", String.valueOf(entry.getValue())));
        }
    }

    @Subcommand("requirement removeall")
    @CommandPermission("auraskills.command.armor.requirement")
    @Description("Removes all armor requirements from the item held.")
    public void onArmorRequirementRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        Requirements requirements = new Requirements(plugin);
        ItemStack item = requirements.removeAllRequirements(ModifierType.ARMOR, player.getInventory().getItemInMainHand());
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ARMOR_REQUIREMENT_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("multiplier add")
    @CommandCompletion("@skills_global @nothing true|false")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("Adds an armor multiplier to the held item to global or a specific skill where value is the percent more XP gained.")
    public void onArmorMultiplierAdd(@Flags("itemheld") Player player, String target, double value, @Default("true") boolean lore) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(target));
        Locale locale = plugin.getUser(player).getLocale();

        Multipliers multipliers = new Multipliers(plugin);
        if (skill != null) { // Add multiplier for specific skill
            for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, item)) {
                if (multiplier.skill() == skill) {
                    player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_ADD_ALREADY_EXISTS, locale),
                            "{target}", skill.getDisplayName(locale)));
                    return;
                }
            }
            if (lore) {
                multipliers.addLore(ModifierType.ARMOR, item, skill, value, locale);
            }
            ItemStack newItem = multipliers.addMultiplier(ModifierType.ARMOR, item, skill, value);
            player.getInventory().setItemInMainHand(newItem);
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_ADD_ADDED, locale),
                    "{target}", skill.getDisplayName(locale), "{value}", String.valueOf(value)));
        } else if (target.equalsIgnoreCase("global")) { // Add multiplier for all skills
            String global = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
            for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, item)) {
                if (multiplier.skill() == null) {
                    player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_ADD_ALREADY_EXISTS, locale),
                            "{target}", global));
                    return;
                }
            }
            if (lore) {
                multipliers.addLore(ModifierType.ARMOR, item, null, value, locale);
            }
            ItemStack newItem = multipliers.addMultiplier(ModifierType.ARMOR, item, null, value);
            player.getInventory().setItemInMainHand(newItem);
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_ADD_ADDED, locale),
                    "{target}", global, "{value}", String.valueOf(value)));
        } else {
            throw new InvalidCommandArgument("Target must be valid skill name or global");
        }
    }

    @Subcommand("multiplier remove")
    @CommandCompletion("@skills_global")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("Removes an armor multiplier of a the specified skill or global from the held item.")
    public void onArmorMultiplierRemove(@Flags("itemheld") Player player, String target) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(target));
        boolean removed = false;

        Multipliers multipliers = new Multipliers(plugin);
        for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, item)) {
            if (multiplier.skill() == skill) {
                item = multipliers.removeMultiplier(ModifierType.ARMOR, item, skill);
                removed = true;
                break;
            }
        }
        player.getInventory().setItemInMainHand(item);
        // Use skill display name if skill is not null, otherwise use global name
        String targetName;
        if (skill != null) {
            targetName = skill.getDisplayName(locale);
        } else if (target.equalsIgnoreCase("global")) {
            targetName = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
        } else {
            throw new InvalidCommandArgument("Target must be valid skill name or global");
        }
        if (removed) {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_REMOVE_REMOVED, locale),
                    "{target}", targetName));
        } else {
            player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_REMOVE_DOES_NOT_EXIST, locale),
                    "{target}", targetName));
        }
    }

    @Subcommand("multiplier list")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("Lists all armor multipliers on the held item.")
    public void onArmorMultiplierList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_LIST_HEADER, locale));
        Multipliers multipliers = new Multipliers(plugin);
        for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, item)) {
            String targetName;
            if (multiplier.skill() != null) {
                targetName = multiplier.skill().getDisplayName(locale);
            } else {
                targetName = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
            }
            message.append("\n").append(TextUtil.replace(plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_LIST_ENTRY, locale),
                    "{target}", targetName, "{value}", String.valueOf(multiplier.value())));
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("multiplier removeall")
    @CommandPermission("auraskills.command.armor.multiplier")
    @Description("Removes all armor multipliers from the item held.")
    public void onArmorMultiplierRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getUser(player).getLocale();
        Multipliers multipliers = new Multipliers(plugin);
        ItemStack item = multipliers.removeAllMultipliers(ModifierType.ARMOR, player.getInventory().getItemInMainHand());
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.ARMOR_MULTIPLIER_REMOVEALL_REMOVED, locale));
    }

}
