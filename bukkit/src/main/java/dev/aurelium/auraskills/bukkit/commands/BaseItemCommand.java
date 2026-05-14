package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.SkillsItem;
import dev.aurelium.auraskills.bukkit.item.SkillsItem.MetaType;
import dev.aurelium.auraskills.bukkit.stat.StatFormat;
import dev.aurelium.auraskills.common.message.type.ACFCoreMessage;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

public class BaseItemCommand {

    protected final AuraSkills plugin;
    protected final StatFormat format;
    protected final String prefixCommandMessage;
    protected final ModifierType commandModifierType;

    public BaseItemCommand(AuraSkills plugin, String prefix, ModifierType type) {
        this.plugin = plugin;
        this.format = new StatFormat(plugin);
        this.prefixCommandMessage = prefix;
        this.commandModifierType = type;
    }

    protected void handlePlayerTarget(
                CommandIssuer issuer,
                Player other,
                BiConsumer<Player, Locale> action) {
        Locale locale = plugin.getLocale(issuer);
        if (other == null) {
            if (!issuer.isPlayer()) {
                issuer.sendMessage(plugin.getMsg(ACFCoreMessage.NOT_ALLOWED_ON_CONSOLE, locale));
                return;
            }

            Player player = issuer.getIssuer();
            if (!checkItemHeld(issuer, player, locale)) {
                issuer.sendMessage(plugin.getMsg(ACFCoreMessage.ERROR_PERFORMING_COMMAND, locale));
                return;
            }

            action.accept(player, locale);
            return;
        }

        if (!checkItemHeld(issuer, other, locale)) {
            issuer.sendMessage(plugin.getMsg(ACFCoreMessage.ERROR_PERFORMING_COMMAND, locale));
            return;
        }

        action.accept(other, locale);
    }

    private boolean checkItemHeld(CommandIssuer issuer, Player player, Locale locale) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            issuer.sendMessage(plugin.getMsg(ACFCoreMessage.ERROR_PERFORMING_COMMAND, locale));
            return false;
        }
        return true;
    }

    protected CommandMessage getCommandMessage(String key) {
        return CommandMessage.valueOf(prefixCommandMessage + "_" + key);
    }

    protected void onItemModifierAdd(CommandIssuer issuer, Player player, Stat stat, double value, Operation operation, boolean lore, boolean overwrite) {
        Locale locale = plugin.getLocale(issuer);
        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        for (StatModifier statModifier : skillsItem.getStatModifiers(commandModifierType)) {
            if (statModifier.stat().equals(stat)) {
                if (!overwrite) {
                    issuer.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(getCommandMessage("MODIFIER_ADD_ALREADY_EXISTS"), locale), stat, locale));
                    return;
                }
                skillsItem.removeModifier(MetaType.MODIFIER, commandModifierType, stat);
                if (lore) {
                    skillsItem.removeModifierLore(stat, locale);
                }
            }
        }
        if (lore) {
            skillsItem.addModifierLore(commandModifierType, stat, value, operation, locale);
        }
        skillsItem.addModifier(MetaType.MODIFIER, commandModifierType, stat, value, operation);
        ItemStack newItem = skillsItem.getItem();
        player.getInventory().setItemInMainHand(newItem);
        issuer.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(getCommandMessage("MODIFIER_ADD_ADDED"), locale), stat, value, operation, locale));
    }

    protected void onItemModifierRemove(CommandIssuer issuer, Player player, Stat stat, boolean lore) {
        Locale locale = plugin.getLocale(issuer);
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean removed = false;
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (StatModifier modifier : skillsItem.getStatModifiers(commandModifierType)) {
            if (modifier.stat() == stat) {
                skillsItem.removeModifier(MetaType.MODIFIER, commandModifierType, stat);
                removed = true;
                break;
            }
        }
        if (lore) {
            skillsItem.removeModifierLore(stat, locale);
        }
        item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        if (removed) {
            issuer.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(getCommandMessage("MODIFIER_REMOVE_REMOVED"), locale), stat, locale));
        } else {
            issuer.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(getCommandMessage("MODIFIER_REMOVE_DOES_NOT_EXIST"), locale), stat, locale));
        }
    }

    protected void onItemModifierList(CommandIssuer issuer, Player player) {
        Locale locale = plugin.getLocale(issuer);
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(getCommandMessage("MODIFIER_LIST_HEADER"), locale));
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (StatModifier modifier : skillsItem.getStatModifiers(commandModifierType)) {
            message.append("\n").append(format.applyPlaceholders(plugin.getMsg(getCommandMessage("MODIFIER_LIST_ENTRY"), locale), modifier, locale));
        }
        issuer.sendMessage(message.toString());
    }

    protected void onItemModifierRemoveAll(CommandIssuer issuer, Player player) {
        Locale locale = plugin.getLocale(issuer);

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        skillsItem.removeAll(MetaType.MODIFIER, commandModifierType);
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(getCommandMessage("MODIFIER_REMOVEALL_REMOVED"), locale));
    }

    protected void onItemTraitAdd(CommandIssuer issuer, Player player, Trait trait, double value, Operation operation, boolean lore, boolean overwrite) {
        Locale locale = plugin.getLocale(issuer);
        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        for (TraitModifier modifier : skillsItem.getTraitModifiers(commandModifierType)) {
            if (modifier.trait().equals(trait)) {
                if (!overwrite) {
                    issuer.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(getCommandMessage("TRAIT_ADD_ALREADY_EXISTS"), locale), trait, locale));
                    return;
                }
                skillsItem.removeModifier(MetaType.TRAIT_MODIFIER, commandModifierType, trait);
                if (lore) {
                    skillsItem.removeModifierLore(trait, locale);
                }
            }
        }
        if (lore) {
            skillsItem.addModifierLore(commandModifierType, trait, value, operation, locale);
        }
        skillsItem.addModifier(MetaType.TRAIT_MODIFIER, commandModifierType, trait, value, operation);
        ItemStack newItem = skillsItem.getItem();
        player.getInventory().setItemInMainHand(newItem);
        issuer.sendMessage(plugin.getPrefix(locale) +
                format.applyPlaceholders(plugin.getMsg(getCommandMessage("TRAIT_ADD_ADDED"), locale), trait, value, operation, locale));
    }

    protected void onItemTraitRemove(CommandIssuer issuer, Player player, Trait trait, boolean lore) {
        Locale locale = plugin.getLocale(issuer);
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean removed = false;
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (TraitModifier modifier : skillsItem.getTraitModifiers(commandModifierType)) {
            if (modifier.trait().equals(trait)) {
                skillsItem.removeModifier(MetaType.TRAIT_MODIFIER, commandModifierType, trait);
                removed = true;
                break;
            }
        }
        if (lore) {
            skillsItem.removeModifierLore(trait, locale);
        }
        item = skillsItem.getItem();
        player.getInventory().setItemInMainHand(item);
        if (removed) {
            issuer.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(getCommandMessage("MODIFIER_REMOVE_REMOVED"), locale), trait, locale));
        } else {
            issuer.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(getCommandMessage("MODIFIER_REMOVE_DOES_NOT_EXIST"), locale), trait, locale));
        }
    }

    protected void onItemTraitList(CommandIssuer issuer, Player player) {
        Locale locale = plugin.getLocale(issuer);
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(getCommandMessage("MODIFIER_LIST_HEADER"), locale));
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (TraitModifier modifier : skillsItem.getTraitModifiers(commandModifierType)) {
            message.append("\n").append(format.applyPlaceholders(plugin.getMsg(getCommandMessage("MODIFIER_LIST_ENTRY"), locale), modifier, locale));
        }
        issuer.sendMessage(message.toString());
    }

    protected void onItemTraitRemoveAll(CommandIssuer issuer, Player player) {
        Locale locale = plugin.getLocale(issuer);

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        skillsItem.removeAll(MetaType.TRAIT_MODIFIER, commandModifierType);
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(getCommandMessage("MODIFIER_REMOVEALL_REMOVED"), locale));
    }

    protected void onItemRequirementAdd(CommandIssuer issuer, Player player, Skill skill, int level, boolean lore, boolean overwrite) {
        Locale locale = plugin.getLocale(issuer);
        ItemStack item = player.getInventory().getItemInMainHand();

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (skillsItem.getRequirements(commandModifierType).containsKey(skill)) {
            if (!overwrite) {
                issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(getCommandMessage("REQUIREMENT_ADD_ALREADY_EXISTS"), locale), "{skill}", skill.getDisplayName(locale)));
                return;
            }
            skillsItem.removeRequirement(commandModifierType, skill);
            if (lore) {
                skillsItem.removeRequirementLore(skill, locale);
            }
        }
        skillsItem.addRequirement(commandModifierType, skill, level);
        if (lore) {
            skillsItem.addRequirementLore(commandModifierType, skill, level, locale);
        }
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(getCommandMessage("REQUIREMENT_ADD_ADDED"), locale),
                "{skill}", skill.getDisplayName(locale),
                "{level}", String.valueOf(level)));
    }

    protected void onItemRequirementRemove(CommandIssuer issuer, Player player, Skill skill, boolean lore) {
        Locale locale = plugin.getLocale(issuer);
        ItemStack item = player.getInventory().getItemInMainHand();

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (skillsItem.getRequirements(commandModifierType).containsKey(skill)) {
            skillsItem.removeRequirement(commandModifierType, skill);
            if (lore) {
                skillsItem.removeRequirementLore(skill, locale);
            }
            item = skillsItem.getItem();

            player.getInventory().setItemInMainHand(item);
            issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(getCommandMessage("REQUIREMENT_REMOVE_REMOVED"), locale),
                    "{skill}", skill.getDisplayName(locale)));
        } else {
            issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(getCommandMessage("REQUIREMENT_REMOVE_DOES_NOT_EXIST"), locale),
                    "{skill}", skill.getDisplayName(locale)));
        }
    }

    protected void onItemRequirementList(CommandIssuer issuer, Player player) {
        Locale locale = plugin.getLocale(issuer);
        issuer.sendMessage(plugin.getMsg(getCommandMessage("REQUIREMENT_LIST_HEADER"), locale));

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (Map.Entry<Skill, Integer> entry : skillsItem.getRequirements(commandModifierType).entrySet()) {
            issuer.sendMessage(TextUtil.replace(plugin.getMsg(getCommandMessage("REQUIREMENT_LIST_ENTRY"), locale),
                    "{skill}", entry.getKey().getDisplayName(locale),
                    "{level}", String.valueOf(entry.getValue())));
        }
    }

    protected void onItemRequirementRemoveAll(CommandIssuer issuer, Player player) {
        Locale locale = plugin.getLocale(issuer);

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeAll(SkillsItem.MetaType.REQUIREMENT, commandModifierType);
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(getCommandMessage("REQUIREMENT_REMOVEALL_REMOVED"), locale));
    }

    protected void onItemMultiplierAdd(CommandIssuer issuer, Player player, String target, double value, boolean lore, boolean overwrite) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(target));
        Locale locale = plugin.getLocale(issuer);

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (skill != null) { // Add multiplier for specific skill
            for (Multiplier multiplier : skillsItem.getMultipliers(commandModifierType)) {
                if (multiplier.skill().equals(skill)) {
                    if (!overwrite) {
                        issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(getCommandMessage("MULTIPLIER_ADD_ALREADY_EXISTS"), locale),
                                "{target}", skill.getDisplayName(locale)));
                        return;
                    }
                    skillsItem.removeMultiplier(commandModifierType, skill);
                    if (lore) {
                        skillsItem.removeMultiplierLore(skill, locale);
                    }
                }
            }
            if (lore) {
                skillsItem.addMultiplierLore(commandModifierType, skill, value, locale);
            }
            skillsItem.addMultiplier(commandModifierType, skill, value);
            ItemStack newItem = skillsItem.getItem();
            player.getInventory().setItemInMainHand(newItem);
            issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(getCommandMessage("MULTIPLIER_ADD_ADDED"), locale),
                    "{target}", skill.getDisplayName(locale), "{value}", String.valueOf(value)));
        } else if (target.equalsIgnoreCase("global")) { // Add multiplier for all skills
            String global = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
            for (Multiplier multiplier : skillsItem.getMultipliers(commandModifierType)) {
                if (multiplier.skill() == null) {
                    if (!overwrite) {
                        issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(getCommandMessage("MULTIPLIER_ADD_ALREADY_EXISTS"), locale),
                                "{target}", global));
                        return;
                    }
                    skillsItem.removeMultiplier(commandModifierType, null);
                    if (lore) {
                        skillsItem.removeMultiplierLore(null, locale);
                    }
                }
            }
            if (lore) {
                skillsItem.addMultiplierLore(commandModifierType, null, value, locale);
            }
            skillsItem.addMultiplier(commandModifierType, null, value);
            ItemStack newItem = skillsItem.getItem();
            player.getInventory().setItemInMainHand(newItem);
            issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(getCommandMessage("MULTIPLIER_ADD_ADDED"), locale),
                    "{target}", global, "{value}", String.valueOf(value)));
        } else {
            throw new InvalidCommandArgument("Target must be valid skill name or global");
        }
    }

    protected void onItemMultiplierRemove(CommandIssuer issuer, Player player, String target, Boolean lore) {
        Locale locale = plugin.getLocale(issuer);
        ItemStack item = player.getInventory().getItemInMainHand();
        Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(target));
        boolean removed = false;

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (Multiplier multiplier : skillsItem.getMultipliers(commandModifierType)) {
            if (multiplier.skill() == skill) {
                skillsItem.removeMultiplier(commandModifierType, skill);
                if (lore) {
                    skillsItem.removeMultiplierLore(skill, locale);
                }
                removed = true;
                break;
            }
        }
        item = skillsItem.getItem();
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
            issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(getCommandMessage("MULTIPLIER_REMOVE_REMOVED"), locale),
                    "{target}", targetName));
        } else {
            issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(getCommandMessage("MULTIPLIER_REMOVE_DOES_NOT_EXIST"), locale),
                    "{target}", targetName));
        }
    }

    protected void onItemMultiplierList(CommandIssuer issuer, Player player) {
        Locale locale = plugin.getLocale(issuer);
        ItemStack item = player.getInventory().getItemInMainHand();
        StringBuilder message = new StringBuilder(plugin.getMsg(getCommandMessage("MULTIPLIER_LIST_HEADER"), locale));
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        for (Multiplier multiplier : skillsItem.getMultipliers(commandModifierType)) {
            String targetName;
            if (multiplier.skill() != null) {
                targetName = multiplier.skill().getDisplayName(locale);
            } else {
                targetName = plugin.getMsg(CommandMessage.MULTIPLIER_GLOBAL, locale);
            }
            message.append("\n").append(TextUtil.replace(plugin.getMsg(getCommandMessage("MULTIPLIER_LIST_ENTRY"), locale),
                    "{target}", targetName, "{value}", String.valueOf(multiplier.value())));
        }
        issuer.sendMessage(message.toString());
    }

    protected void onItemMultiplierRemoveAll(CommandIssuer issuer, Player player) {
        Locale locale = plugin.getLocale(issuer);

        ItemStack item = player.getInventory().getItemInMainHand();
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeAll(SkillsItem.MetaType.MULTIPLIER, commandModifierType);
        item = skillsItem.getItem();

        player.getInventory().setItemInMainHand(item);
        issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(getCommandMessage("MULTIPLIER_REMOVEALL_REMOVED"), locale));
    }

}
