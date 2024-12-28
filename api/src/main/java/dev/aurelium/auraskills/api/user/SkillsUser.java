package dev.aurelium.auraskills.api.user;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
     * Adds XP to a skill as if earned in game with a specific XP source. The final amount of XP
     * added to the player may be modified by abilities and multipliers.
     *
     * @param skill the skill to add XP to
     * @param amountToAdd the amount of XP to add
     * @param source the source of the XP
     */
    void addSkillXp(Skill skill, double amountToAdd, XpSource source);

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
     * Sets the level of a skill.
     *
     * @param skill the skill to set the level of
     * @param level the level to set to
     * @param refresh whether to refresh stats, permissions, rewards, and item modifiers to account for the change in skill level
     */
    void setSkillLevel(Skill skill, int level, boolean refresh);

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
     * Attempts to consume the specified amount of mana, simulating using a mana ability.
     * Will only consume if the user's mana is greater than or equal to amount. If the player does not
     * have enough mana, a "Not enough mana" message will be sent to the user's action bar. Does not send
     * any message if successful, you must handle that.
     *
     * @param amount the amount to consume
     * @return true if the user had enough mana and the operation was successful, false if not
     */
    boolean consumeMana(double amount);

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
     * Adds a trait modifier to the player.
     *
     * @param traitModifier the trait modifier
     */
    void addTraitModifier(TraitModifier traitModifier);

    /**
     * Removes a trait modifier from the player with a given name.
     *
     * @param name the name of the trait modifier to remove
     */
    void removeTraitModifier(String name);

    /**
     * Gets a trait modifier from its name
     *
     * @param name the name of the modifier
     * @return the trait modifier, or null if none exists with the name.
     */
    @Nullable
    TraitModifier getTraitModifier(String name);

    /**
     * Gets a map of all the user's trait modifiers.
     *
     * @return the map of all trait modifiers
     */
    Map<String, TraitModifier> getTraitModifiers();

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

    /**
     * Gets whether the player the permission node to use a skill.
     * The checked node is the format auraskills.skill.[skillName], which is true by default.
     *
     * @param skill the skill to check
     * @return whether the player has the permission
     */
    boolean hasSkillPermission(Skill skill);

    /**
     * Gets the player's active jobs. Returns an empty set if the user has no jobs or
     * if jobs are not enabled. The returned set cannot be modified, use {@link #addJob(Skill)}
     * and {@link #removeJob(Skill)} to change player jobs.
     *
     * @return the set of active jobs
     */
    Set<Skill> getJobs();

    /**
     * Adds a skill as an active job.
     *
     * @param job the skill to add as a job
     */
    void addJob(Skill job);

    /**
     * Removes a skill from the player's active jobs.
     *
     * @param job the job to remove
     */
    void removeJob(Skill job);

    /**
     * Removes all active jobs.
     */
    void clearAllJobs();

    /**
     * Gets the maximum number of jobs the player can have active at once.
     *
     * @return the jobs limit
     */
    int getJobLimit();

    /**
     * Sends an action bar to the user and pauses other AuraSkills action bars for the default duration (750ms or 15 ticks).
     *
     * @param message the message to send
     */
    void sendActionBar(String message);

    /**
     * Sends an action bar to the user and pauses other AuraSkills action bars for the specified duration.
     *
     * @param message the action bar message
     * @param duration the duration
     * @param timeUnit the time unit of the duration
     */
    void sendActionBar(String message, int duration, TimeUnit timeUnit);

    /**
     * Pauses AuraSkills action bars for a certain duration. While idle and xp action bars will be paused, others like ability
     * messages may still be sent.
     *
     * @param duration the duration to pause
     * @param timeUnit the time unit of the duration
     */
    void pauseActionBar(int duration, TimeUnit timeUnit);

    /**
     * Saves the user to persistent storage asynchronously. This is only recommended if the SkillsUser represents an
     * offline user, as changes for online users are saved automatically on logout.
     *
     * @param removeFromMemory Whether to remove the user from memory after saving. Will not work if the user is online.
     * @return A future signaling when the saving is complete. The future's value is true if successful, and false if an
     * exception was thrown.
     */
    CompletableFuture<Boolean> save(boolean removeFromMemory);

}
