package dev.aurelium.auraskills.api.user;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public interface SkillsUser {

    /**
     * Gets the Minecraft UUID of the player.
     *
     * @return the player's UUID
     */
    UUID getUuid();

    /**
     * Whether the instance represents an online user that is loaded into memory.
     * If the user is not loaded, get methods will return default values and set methods
     * will have no effect.
     *
     * @return whether the user is loaded
     */
    boolean isLoaded();

    /**
     * Gets the current amount of XP in a skill. The amount ranges from 0 to the XP required
     * to progress to the next skill level.
     *
     * @param skill the skill to get XP from
     * @return the XP amount
     */
    double getSkillXp(Skill skill);

    /**
     * Adds XP to a skill as if earned in game. The final amount of XP added to the player
     * may be modified by abilities and multipliers.
     *
     * @param skill the skill to add XP to
     * @param amountToAdd the amount of XP to add
     */
    void addSkillXp(Skill skill, double amountToAdd);

    /**
     * Adds an exact amount of XP to a skill, bypassing in-game abilities and multipliers.
     *
     * @param skill the skill to add XP to
     * @param amountToAdd the exact amount of XP to add
     */
    void addSkillXpRaw(Skill skill, double amountToAdd);

    /**
     * Sets the XP of a skill to the given amount.
     *
     * @param skill the skill to set the XP of
     * @param amount the amount of XP to set
     */
    void setSkillXp(Skill skill, double amount);

    /**
     * Gets the level of a skill.
     *
     * @param skill the skill to get the level of
     * @return the skill level
     */
    int getSkillLevel(Skill skill);

    /**
     * Sets the level of a skill.
     *
     * @param skill the skill to set the level of
     * @param level the level to set to
     */
    void setSkillLevel(Skill skill, int level);

    /**
     * Gets the user's average skill level of all enabled skills.
     *
     * @return the skill average
     */
    double getSkillAverage();

    /**
     * Gets the level of a stat.
     *
     * @param stat the stat to get the level of
     * @return the level of the stat
     */
    double getStatLevel(Stat stat);

    /**
     * Gets the level of a stat without any stat modifiers. The base level is the amount
     * obtained only from the permanent rewards for leveling skills.
     *
     * @param stat the stat to get the base level of
     * @return the base stat level
     */
    double getBaseStatLevel(Stat stat);

    /**
     * Gets the current mana of the player.
     *
     * @return the current mana
     */
    double getMana();

    /**
     * Gets the maximum mana of the player.
     *
     * @return the maximum mana
     */
    double getMaxMana();

    /**
     * Sets the mana of the player.
     *
     * @param mana the amount of mana to set
     */
    void setMana(double mana);

    /**
     * Gets the power level of the player. The power level is the sum of all skill levels.
     *
     * @return the power level
     */
    int getPowerLevel();

    /**
     * Adds a stat modifier to the player. Stat modifiers are temporary changes to a stat
     * that require a name to be identified and removed.
     *
     * @param statModifier the stat modifier to add
     */
    void addStatModifier(StatModifier statModifier);

    /**
     * Removes a stat modifier from the player.
     *
     * @param name the name of the stat modifier to remove
     */
    void removeStatModifier(String name);

    /**
     * Gets a stat modifier from its name.
     *
     * @param name the name of the modifier
     * @return the stat modifier, or null if none exists with the name.
     */
    @Nullable
    StatModifier getStatModifier(String name);

    /**
     * Gets a map of all the user's stat modifiers.
     *
     * @return the map of all stat modifiers
     */
    Map<String, StatModifier> getStatModifiers();

    /**
     * Gets the total level of a trait, including non-plugin base values.
     *
     * @param trait the trait to get the level of
     * @return the total effective level
     */
    double getEffectiveTraitLevel(Trait trait);

    /**
     * Gets the level of a trait from only the plugin's stats and trait modifiers.
     *
     * @param trait the trait to get the level of
     * @return the bonus trait level
     */
    double getBonusTraitLevel(Trait trait);

    /**
     * Gets the level of an ability.
     *
     * @param ability the ability to get the level of
     * @return the level of the ability
     */
    int getAbilityLevel(Ability ability);

    /**
     * Gets the level of a mana ability.
     *
     * @param manaAbility the mana ability to get the level of
     * @return the level of the mana ability
     */
    int getManaAbilityLevel(ManaAbility manaAbility);

    /**
     * Gets the locale of the player, or the server default locale if the
     * player has not set a locale.
     *
     * @return the locale of the player
     */
    Locale getLocale();

}
