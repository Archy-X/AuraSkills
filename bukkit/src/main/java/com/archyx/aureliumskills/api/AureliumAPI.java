package com.archyx.aureliumskills.api;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.modifier.Modifiers;
import dev.aurelium.auraskills.bukkit.modifier.Multipliers;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AureliumAPI {

    private static AuraSkills plugin;

    public static void setPlugin(AuraSkills plugin) {
        if (AureliumAPI.plugin == null) {
            AureliumAPI.plugin = plugin;
        } else {
            throw new IllegalStateException("The AureliumSkills API is already registered");
        }
    }

    /**
     * Gets the current mana of a player
     * @return the current mana of a player
     */
    public static double getMana(Player player) {
        return plugin.getUser(player).getMana();
    }

    /**
     * Gets the max mana of a player
     * @return the max mana of a player
     */
    public static double getMaxMana(Player player) {
        return plugin.getUser(player).getMaxMana();
    }

    /**
     * Gets the amount of mana a player regenerates every second
     * @return the mana regeneration per second of a player
     */
    public static double getManaRegen(Player player) {
        return plugin.getUser(player).getEffectiveTraitLevel(Traits.MANA_REGEN);
    }

    /**
     * Sets a player's mana to an amount
     */
    public static void setMana(Player player, double amount) {
        plugin.getUser(player).setMana(amount);
    }

    /**
     * Adds Skill XP to a player for a certain skill, and includes multiplier permissions
     */
    public static void addXp(Player player, Skill skill, double amount) {
        plugin.getLevelManager().addXp(plugin.getUser(player), Skills.valueOf(skill.name()), amount);
    }

    /**
     * Adds Skill XP to a player for a certain skill, without multipliers
     */
    public static void addXpRaw(Player player, Skill skill, double amount) {
        Skills skills = Skills.valueOf(skill.name());
        plugin.getUser(player).addSkillXp(skills, amount);
        plugin.getLevelManager().checkLevelUp(plugin.getUser(player), skills);
    }

    /**
     * Gets the skill level of a player
     * @return the skill level of a player, or 1 if player does not have a skills profile
     */
    public static int getSkillLevel(Player player, Skill skill) {
        return plugin.getUser(player).getSkillLevel(Skills.valueOf(skill.name()));
    }

    /**
     * Gets the total skill level of a player
     * @return the total skill level of a player, will display number of skills available if player does not have a skills profile
     */
    public static int getTotalLevel(Player player) {
        int totalLevel = 0;
        User user = plugin.getUser(player);
        for (Skills skill : Skills.values()) {
            totalLevel += user.getSkillLevel(skill);
        }
        return totalLevel;
    }

    /**
     * Gets the skill xp of a player
     * @param player The player to get from
     * @param skill The skill to get
     * @return The current skill xp
     */
    public static double getXp(Player player, Skill skill) {
        return plugin.getUser(player).getSkillXp(Skills.valueOf(skill.name()));
    }

    /**
     * Gets the stat level of a player
     * @param player The player to get from
     * @param stat The stat to get
     * @return The stat level
     */
    public static double getStatLevel(Player player, Stat stat) {
        return plugin.getUser(player).getStatLevel(Stats.valueOf(stat.name()));
    }

    /**
     * Adds a stat modifier to a player
     * @param player The player to add to
     * @param name The name of the stat modifier
     * @param stat The stat to add to
     * @param value The value of the modifier
     * @return true if a modifier was added, false if the player does not have a skills profile
     */
    public static boolean addStatModifier(Player player, String name, Stat stat, double value) {
        plugin.getUser(player).addStatModifier(new StatModifier(name, Stats.valueOf(stat.name()), value));
        return true;
    }

    /**
     * Removes a stat modifier from a player
     * @param player The player to remove from
     * @param name The name of the stat modifier
     * @return true if the operation was successful, false if the stat modifier was not found or the player does not have a skills profile
     */
    public static boolean removeStatModifier(Player player, String name) {
        plugin.getUser(player).removeStatModifier(name);
        return true;
    }

    /**
     * Adds an item modifier to an item, with optional lore. This does NOT change the item passed in directly,
     * you must use the returned ItemStack. This means the original ItemStack passed in is not changed at all, a new
     * one is created.
     * @param item The original item, will not be changed by the method
     * @param stat The stat to add (Use Stats enum for default stats)
     * @param value The value of the stat to add
     * @param lore Whether to add lore. Added lore will use the default language.
     * @return A new ItemStack with the item modifier
     */
    public static ItemStack addItemModifier(ItemStack item, Stat stat, double value, boolean lore) {
        Modifiers modifiers = new Modifiers(plugin);
        Stats stats = Stats.valueOf(stat.name());
        ItemStack modifiedItem = modifiers.addModifier(ModifierType.ITEM, item, stats, value);
        if (lore) {
            modifiers.addLore(ModifierType.ITEM, modifiedItem, stats, value, plugin.getDefaultLanguage());
        }
        return modifiedItem;
    }

    /**
     * Adds an armor modifier to an item, with optional lore. This does NOT change the item passed in directly,
     * you must use the returned ItemStack. This means the original ItemStack passed in is not changed at all, a new
     * one is created.
     * @param item The original item, will not be changed by the method
     * @param stat The stat to add (Use Stats enum for default stats)
     * @param value The value of the stat to add
     * @param lore Whether to add lore. Added lore will use the default language.
     * @return A new ItemStack with the armor modifier
     */
    public static ItemStack addArmorModifier(ItemStack item, Stat stat, double value, boolean lore) {
        Modifiers modifiers = new Modifiers(plugin);
        Stats stats = Stats.valueOf(stat.name());
        ItemStack modifiedItem = modifiers.addModifier(ModifierType.ARMOR, item, stats, value);
        if (lore) {
            modifiers.addLore(ModifierType.ARMOR, modifiedItem, stats, value, plugin.getDefaultLanguage());
        }
        return modifiedItem;
    }

    /**
     * Adds an item multiplier to an item, with optional lore. This does NOT change the item passed in directly,
     * you must use the returned ItemStack. This means the original ItemStack passed in is not changed at all, a new
     * one is created.
     * @param item The original item, will not be changed by the method
     * @param skill The skill to add (Use Skills enum for default skills)
     * @param value The value of the multiplier (in percentage points) to add
     * @param lore Whether to add lore. Added lore will use the default language.
     * @return A new ItemStack with the item modifier
     */
    public static ItemStack addItemMultiplier(ItemStack item, Skill skill, double value, boolean lore) {
        Multipliers multipliers = new Multipliers(plugin);
        Skills skills = Skills.valueOf(skill.name());
        ItemStack modifiedItem = multipliers.addMultiplier(ModifierType.ITEM, item, skills, value);
        if (lore) {
            multipliers.addLore(ModifierType.ITEM, modifiedItem, skills, value, plugin.getDefaultLanguage());
        }
        return modifiedItem;
    }

    /**
     * Adds an armor multiplier to an item, with optional lore. This does NOT change the item passed in directly,
     * you must use the returned ItemStack. This means the original ItemStack passed in is not changed at all, a new
     * one is created.
     * @param item The original item, will not be changed by the method
     * @param skill The skill to add (Use Skills enum for default skills)
     * @param value The value of the multiplier (in percentage points) to add
     * @param lore Whether to add lore. Added lore will use the default language.
     * @return A new ItemStack with the armor modifier
     */
    public static ItemStack addArmorMultiplier(ItemStack item, Skill skill, double value, boolean lore) {
        Multipliers multipliers = new Multipliers(plugin);
        Skills skills = Skills.valueOf(skill.name());
        ItemStack modifiedItem = multipliers.addMultiplier(ModifierType.ARMOR, item, skills, value);
        if (lore) {
            multipliers.addLore(ModifierType.ARMOR, modifiedItem, skills, value, plugin.getDefaultLanguage());
        }
        return modifiedItem;
    }

}
