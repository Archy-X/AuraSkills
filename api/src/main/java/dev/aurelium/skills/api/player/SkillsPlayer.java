package dev.aurelium.skills.api.player;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.api.stat.StatModifier;

public interface SkillsPlayer {

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
     * @return The base stat level
     */
    double getBaseStatLevel(Stat stat);

    double getMana();

    double getMaxMana();

    void setMana(double mana);

    int getPowerLevel();

    void addStatModifier(StatModifier statModifier);

    void removeStatModifier(String name);

    int getAbilityLevel(Ability ability);

    int getManaAbilityLevel(ManaAbility manaAbility);

}
