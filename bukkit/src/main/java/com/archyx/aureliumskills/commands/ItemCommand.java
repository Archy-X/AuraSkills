package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.LevelerMessage;
import com.archyx.aureliumskills.modifier.*;
import com.archyx.aureliumskills.requirement.Requirements;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.misc.KeyIntPair;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;

@CommandAlias("%skills_alias")
@Subcommand("item")
public class ItemCommand extends BaseCommand {

    private final AureliumSkills plugin;

    public ItemCommand(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("modifier add")
    @CommandCompletion("@stats @nothing false|true")
    @CommandPermission("aureliumskills.item.modifier.add")
    @Description("Adds an item stat modifier to the item held, along with lore by default.")
    public void onItemModifierAdd(@Flags("itemheld") Player player, Stat stat, double value, @Default("true") boolean lore) {
        Locale locale = plugin.getLang().getLocale(player);
        ItemStack item = player.getInventory().getItemInMainHand();
        Modifiers modifiers = new Modifiers(plugin);
        for (StatModifier statModifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
            if (statModifier.getStat() == stat) {
                player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ITEM_MODIFIER_ADD_ALREADY_EXISTS, locale), stat, locale));
                return;
            }
        }
        if (lore) {
            modifiers.addLore(ModifierType.ITEM, item, stat, value, locale);
        }
        ItemStack newItem = modifiers.addModifier(ModifierType.ITEM, item, stat, value);
        player.getInventory().setItemInMainHand(newItem);
        player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ITEM_MODIFIER_ADD_ADDED, locale), stat, value, locale));
    }

    @Subcommand("modifier remove")
    @CommandCompletion("@stats false|true")
    @CommandPermission("aureliumskills.item.modifier.remove")
    @Description("Removes an item stat modifier from the item held, and the lore associated with it by default.")
    public void onItemModifierRemove(@Flags("itemheld") Player player, Stat stat, @Default("true") boolean lore) {
        Locale locale = plugin.getLang().getLocale(player);
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean removed = false;
        Modifiers modifiers = new Modifiers(plugin);
        for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
            if (modifier.getStat() == stat) {
                item = modifiers.removeModifier(ModifierType.ITEM, item, stat);
                removed = true;
                break;
            }
        }
        if (lore) {
            modifiers.removeLore(item, stat, locale);
        }
        player.getInventory().setItemInMainHand(item);
        if (removed) {
            player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ITEM_MODIFIER_REMOVE_REMOVED, locale), stat, locale));
        }
        else {
            player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ITEM_MODIFIER_REMOVE_DOES_NOT_EXIST, locale), stat, locale));
        }
    }

    @Subcommand("modifier list")
    @CommandPermission("aureliumskills.item.modifier.list")
    @Description("Lists all item stat modifiers on the item held.")
    public void onItemModifierList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getLang().getLocale(player);
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(Lang.getMessage(CommandMessage.ITEM_MODIFIER_LIST_HEADER, locale));
        Modifiers modifiers = new Modifiers(plugin);
        for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
            message.append("\n").append(StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ITEM_MODIFIER_LIST_ENTRY, locale), modifier, locale));
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("modifier removeall")
    @CommandPermission("aureliumskills.item.modifier.removall")
    @Description("Removes all item stat modifiers from the item held.")
    public void onItemModifierRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getLang().getLocale(player);
        Modifiers modifiers = new Modifiers(plugin);
        ItemStack item = modifiers.removeAllModifiers(ModifierType.ITEM, player.getInventory().getItemInMainHand());
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_MODIFIER_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("requirement add")
    @CommandPermission("aureliumskills.item.requirement.add")
    @CommandCompletion("@skills @nothing false|true")
    @Description("Adds an item requirement to the item held, along with lore by default.")
    public void onItemRequirementAdd(@Flags("itemheld") Player player, Skill skill, int level, @Default("true") boolean lore) {
        Locale locale = plugin.getLang().getLocale(player);
        ItemStack item = player.getInventory().getItemInMainHand();
        Requirements requirements = new Requirements(plugin);
        if (requirements.hasRequirement(ModifierType.ITEM, item, skill)) {
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_ADD_ALREADY_EXISTS, locale), "{skill}", skill.getDisplayName(locale)));
            return;
        }
        item = requirements.addRequirement(ModifierType.ITEM, item, skill, level);
        if (lore) {
            requirements.addLore(ModifierType.ITEM, item, skill, level, locale);
        }
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_ADD_ADDED, locale),
                "{skill}", skill.getDisplayName(locale),
                "{level}", String.valueOf(level)));
    }

    @Subcommand("requirement remove")
    @CommandPermission("aureliumskills.item.requirement.remove")
    @CommandCompletion("@skills false|true")
    @Description("Removes an item requirement from the item held, and the lore associated with it by default.")
    public void onItemRequirementRemove(@Flags("itemheld") Player player, Skill skill, @Default("true") boolean lore) {
        Locale locale = plugin.getLang().getLocale(player);
        ItemStack item = player.getInventory().getItemInMainHand();
        Requirements requirements = new Requirements(plugin);
        if (requirements.hasRequirement(ModifierType.ITEM, item, skill)) {
            item = requirements.removeRequirement(ModifierType.ITEM, item, skill);
            if (lore) {
                requirements.removeLore(item, skill);
            }
            player.getInventory().setItemInMainHand(item);
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_REMOVE_REMOVED, locale),
                    "{skill}", skill.getDisplayName(locale)));
        }
        else {
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_REMOVE_DOES_NOT_EXIST, locale),
                    "{skill}", skill.getDisplayName(locale)));
        }
    }

    @Subcommand("requirement list")
    @CommandPermission("aureliumskills.item.requirement.list")
    @Description("Lists the item requirements on the item held.")
    public void onItemRequirementList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getLang().getLocale(player);
        player.sendMessage(Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_LIST_HEADER, locale));
        Requirements requirements = new Requirements(plugin);
        for (Map.Entry<Skill, Integer> entry : requirements.getRequirements(ModifierType.ITEM, player.getInventory().getItemInMainHand()).entrySet()) {
            player.sendMessage(TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_LIST_ENTRY, locale),
                    "{skill}", entry.getKey().getDisplayName(locale),
                    "{level}", String.valueOf(entry.getValue())));
        }
    }

    @Subcommand("requirement removeall")
    @CommandPermission("aureliumskills.item.requirement.removeall")
    @Description("Removes all item requirements from the item held.")
    public void onItemRequirementRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getLang().getLocale(player);
        Requirements requirements = new Requirements(plugin);
        ItemStack item = requirements.removeAllRequirements(ModifierType.ITEM, player.getInventory().getItemInMainHand());
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_REMOVEALL_REMOVED, locale));
    }

    @Subcommand("register")
    @CommandPermission("aureliumskills.item.register")
    public void onItemRegister(@Flags("itemheld") Player player, String key) {
        Locale locale = plugin.getLang().getLocale(player);
        if (key.contains(" ")) { // Disallow spaces in key name
            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_REGISTER_NO_SPACES, locale));
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (plugin.getItemRegistry().getItem(key) == null) { // Check that no item has been registered on the key
            plugin.getItemRegistry().register(key, item);
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_REGISTER_REGISTERED, locale), "{key}", key));
        } else {
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_REGISTER_ALREADY_REGISTERED, locale), "{key}", key));
        }
    }

    @Subcommand("unregister")
    @CommandPermission("aureliumskills.item.register")
    @CommandCompletion("@item_keys")
    public void onItemUnregister(Player player, String key) {
        Locale locale = plugin.getLang().getLocale(player);
        if (plugin.getItemRegistry().getItem(key) != null) { // Check that there is an item registered on the key
            plugin.getItemRegistry().unregister(key);
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_UNREGISTER_UNREGISTERED, locale), "{key}", key));
        } else {
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_UNREGISTER_NOT_REGISTERED, locale), "{key}", key));
        }
    }

    @Subcommand("give")
    @CommandPermission("aureliumskills.item.give")
    @CommandCompletion("@players @item_keys")
    public void onItemGive(CommandSender sender, @Flags("other") Player player, String key, @Default("-1") int amount) {
        ItemStack item = plugin.getItemRegistry().getItem(key);
        Locale locale = plugin.getLang().getLocale(sender);
        if (item != null) {
            if (amount != -1) {
                item.setAmount(amount);
            }
            ItemStack leftoverItem = ItemUtils.addItemToInventory(player, item);
            sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_GIVE_SENDER, locale),
                    "{amount}", String.valueOf(item.getAmount()), "{key}", key, "{player}", player.getName()));
            if (!sender.equals(player)) {
                player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_GIVE_RECEIVER, locale),
                        "{amount}", String.valueOf(item.getAmount()), "{key}", key));
            }
            // Add to unclaimed items if leftover
            if (leftoverItem != null) {
                PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                if (playerData != null) {
                    playerData.getUnclaimedItems().add(new KeyIntPair(key, leftoverItem.getAmount()));
                    player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(LevelerMessage.UNCLAIMED_ITEM, locale));
                }
            }
        } else {
            sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_UNREGISTER_NOT_REGISTERED, locale), "{key}", key));
        }
    }

    @Subcommand("multiplier add")
    @CommandCompletion("@skills_global @nothing true|false")
    @CommandPermission("aureliumskills.item.multiplier.add")
    @Description("Adds an item multiplier to the held item to global or a specific skill where value is the percent more XP gained.")
    public void onItemMultiplierAdd(@Flags("itemheld") Player player, String target, double value, @Default("true") boolean lore) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Skill skill = plugin.getSkillRegistry().getSkill(target);
        Locale locale = plugin.getLang().getLocale(player);

        Multipliers multipliers = new Multipliers(plugin);
        if (skill != null) { // Add multiplier for specific skill
            for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, item)) {
                if (multiplier.getSkill() == skill) {
                    player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_MULTIPLIER_ADD_ALREADY_EXISTS, locale),
                            "{target}", skill.getDisplayName(locale)));
                    return;
                }
            }
            if (lore) {
                multipliers.addLore(ModifierType.ITEM, item, skill, value, locale);
            }
            ItemStack newItem = multipliers.addMultiplier(ModifierType.ITEM, item, skill, value);
            player.getInventory().setItemInMainHand(newItem);
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_MULTIPLIER_ADD_ADDED, locale),
                    "{target}", skill.getDisplayName(locale), "{value}", String.valueOf(value)));
        } else if (target.equalsIgnoreCase("global")) { // Add multiplier for all skills
            String global = Lang.getMessage(CommandMessage.MULTIPLIER_GLOBAL, locale);
            for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, item)) {
                if (multiplier.getSkill() == null) {
                    player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_MULTIPLIER_ADD_ALREADY_EXISTS, locale),
                            "{target}", global));
                    return;
                }
            }
            if (lore) {
                multipliers.addLore(ModifierType.ITEM, item, null, value, locale);
            }
            ItemStack newItem = multipliers.addMultiplier(ModifierType.ITEM, item, null, value);
            player.getInventory().setItemInMainHand(newItem);
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_MULTIPLIER_ADD_ADDED, locale),
                    "{target}", global, "{value}", String.valueOf(value)));
        } else {
            throw new InvalidCommandArgument("Target must be valid skill name or global");
        }
    }

    @Subcommand("multiplier remove")
    @CommandCompletion("@skills_global")
    @CommandPermission("aureliumskills.item.multiplier.remove")
    @Description("Removes an item multiplier of a the specified skill or global from the held item.")
    public void onItemMultiplierRemove(@Flags("itemheld") Player player, String target) {
        Locale locale = plugin.getLang().getLocale(player);
        ItemStack item = player.getInventory().getItemInMainHand();
        Skill skill = plugin.getSkillRegistry().getSkill(target);
        boolean removed = false;

        Multipliers multipliers = new Multipliers(plugin);
        for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, item)) {
            if (multiplier.getSkill() == skill) {
                item = multipliers.removeMultiplier(ModifierType.ITEM, item, skill);
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
            targetName = Lang.getMessage(CommandMessage.MULTIPLIER_GLOBAL, locale);
        } else {
            throw new InvalidCommandArgument("Target must be valid skill name or global");
        }
        if (removed) {
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_MULTIPLIER_REMOVE_REMOVED, locale),
                    "{target}", targetName));
        } else {
            player.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_MULTIPLIER_REMOVE_DOES_NOT_EXIST, locale),
                    "{target}", targetName));
        }
    }

    @Subcommand("multiplier list")
    @CommandPermission("aureliumskills.item.multiplier.list")
    @Description("Lists all item multipliers on the held item.")
    public void onItemMultiplierList(@Flags("itemheld") Player player) {
        Locale locale = plugin.getLang().getLocale(player);
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(Lang.getMessage(CommandMessage.ITEM_MULTIPLIER_LIST_HEADER, locale));
        Multipliers multipliers = new Multipliers(plugin);
        for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, item)) {
            String targetName;
            if (multiplier.getSkill() != null) {
                targetName = multiplier.getSkill().getDisplayName(locale);
            } else {
                targetName = Lang.getMessage(CommandMessage.MULTIPLIER_GLOBAL, locale);
            }
            message.append("\n").append(TextUtil.replace(Lang.getMessage(CommandMessage.ITEM_MULTIPLIER_LIST_ENTRY, locale),
                    "{target}", targetName, "{value}", String.valueOf(multiplier.getValue())));
        }
        player.sendMessage(message.toString());
    }

    @Subcommand("multiplier removeall")
    @CommandPermission("aureliumskills.item.multiplier.removeall")
    @Description("Removes all item multipliers from the item held.")
    public void onItemMultiplierRemoveAll(@Flags("itemheld") Player player) {
        Locale locale = plugin.getLang().getLocale(player);
        Multipliers multipliers = new Multipliers(plugin);
        ItemStack item = multipliers.removeAllMultipliers(ModifierType.ITEM, player.getInventory().getItemInMainHand());
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_MULTIPLIER_REMOVEALL_REMOVED, locale));
    }

}
