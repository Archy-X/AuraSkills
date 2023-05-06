package dev.auramc.auraskills.api.player;

import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.stat.Stat;
import dev.auramc.auraskills.api.stat.StatModifier;

import java.util.Locale;
import java.util.UUID;

public interface SkillsPlayer {

    /**
     * Gets the Minecraft UUID of the player.
     *
     * @return The player's UUID
     */
    UUID getUuid();

    /**
     * Gets the current amount of XP in a skill. The amount ranges from 0 to the XP required
     * to progress to the next skill level.
     *
     * @param skill The skill to get XP from
     * @return The XP amount
     */
    double getSkillXp(Skill skill);

    /**
     * Adds XP to a skill as if earned in game. The final amount of XP added to the player
     * may be modified by abilities and multipliers.
     *
     * @param skill The skill to add XP to
     * @param amountToAdd The amount of XP to add
     */
    void addSkillXp(Skill skill, double amountToAdd);

    /**
     * Adds an exact amount of XP to a skill, bypassing in-game abilities and multipliers.
     *
     * @param skill The skill to add XP to
     * @param amountToAdd The exact amount of XP to add
     */
    void addSkillXpRaw(Skill skill, double amountToAdd);

    /**
     * Sets the XP of a skill to the given amount.
     *
     * @param skill The skill to set the XP of
     * @param amount The amount of XP to set
     */
    void setSkillXp(Skill skill, double amount);

    /**
     * Gets the level of a skill.
     *
     * @param skill The skill to get the level of
     * @return The skill level
     */
    int getSkillLevel(Skill skill);

    /**
     * Sets the level of a skill.
     *
     * @param skill The skill to set the level of
     * @param level The level to set to
     */
    void setSkillLevel(Skill skill, int level);

    /**
     * Gets the level of a stat.
     *
     * @param stat The stat to get the level of
     * @return The level of the stat
     */
    double getStatLevel(Stat stat);

    /**
     * Gets the level of a stat without any stat modifiers. The base level is the amount
     * obtained only from the permanent rewards for leveling skills.
     *
     * @param stat The stat to get the base level of
     * @return The base stat level
     */
    double getBaseStatLevel(Stat stat);

    /**
     * Gets the current mana of the player.
     *
     * @return The current mana
     */
    double getMana();

    /**
     * Gets the maximum mana of the player.
     *
     * @return The maximum mana
     */
    double getMaxMana();

    /**
     * Sets the mana of the player.
     *
     * @param mana The amount of mana to set
     */
    void setMana(double mana);

    /**
     * Gets the power level of the player. The power level is the sum of all skill levels.
     *
     * @return The power level
     */
    int getPowerLevel();

    /**
     * Adds a stat modifier to the player. Stat modifiers are temporary changes to a stat
     * that require a name to be identified and removed.
     *
     * @param statModifier The stat modifier to add
     */
    void addStatModifier(StatModifier statModifier);

    /**
     * Removes a stat modifier from the player.
     *
     * @param name The name of the stat modifier to remove
     */
    void removeStatModifier(String name);

    /**
     * Gets the level of an ability.
     *
     * @param ability The ability to get the level of
     * @return The level of the ability
     */
    int getAbilityLevel(Ability ability);

    /**
     * Gets the level of a mana ability.
     *
     * @param manaAbility The mana ability to get the level of
     * @return The level of the mana ability
     */
    int getManaAbilityLevel(ManaAbility manaAbility);

    /**
     * Gets the locale of the player, or the server default locale if the
     * player has not set a locale.
     *
     * @return The locale of the player
     */
    Locale getLocale();

}
