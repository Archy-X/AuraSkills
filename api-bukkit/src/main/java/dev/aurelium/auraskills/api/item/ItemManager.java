package dev.aurelium.auraskills.api.item;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.Stats;
import org.bukkit.inventory.ItemStack;

public interface ItemManager {

    /**
     * Adds a modifier to an item, with optional lore. This does not change the item passed in directly,
     * you must use the returned ItemStack. This means the original ItemStack passed in is not changed at all, a new
     * one is created.
     *
     * @param item the original item, will not be changed by the method
     * @param type the {@link ModifierType} to add
     * @param stat the stat to add (Use {@link Stats} enum for default stats)
     * @param value the value of the stat to add
     * @param lore whether to add lore
     * @return a new ItemStack with the modifier
     */
    ItemStack addModifier(ItemStack item, ModifierType type, Stat stat, double value, boolean lore);

    /**
     * Adds a multiplier to an item, with optional lore. This does not change the item passed in directly,
     * you must use the returned ItemStack. This means the original ItemStack passed in is not changed at all, a new
     * one is created.
     *
     * @param item the original item, will not be changed by the method
     * @param type the {@link ModifierType} to add
     * @param skill the skill to add (Use {@link Skills} enum for default skills)
     * @param value the value of the multiplier (in percentage points) to add
     * @param lore whether to add lore
     * @return a new ItemStack with the multiplier
     */
    ItemStack addMultiplier(ItemStack item, ModifierType type, Skill skill, double value, boolean lore);

    /**
     * Adds a skill requirement to use an item, with optional lore. This does not change the item passed in
     * directly, you must use the returned ItemStack. This means the original ItemStack passed in is not changed at all, a new
     * one is created.
     *
     * @param item the original item, will not be changed by the method
     * @param type the {@link ModifierType} to add
     * @param skill the skill the requirement is for (Use {@link Skills} enum for default skills)
     * @param level the skill level required to use the item
     * @param lore whether to add lore
     * @return a new ItemStack with the requirement
     */
    ItemStack addRequirement(ItemStack item, ModifierType type, Skill skill, int level, boolean lore);

}
