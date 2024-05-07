package dev.aurelium.auraskills.api.mana;

import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.option.Optioned;

import java.util.Locale;

public interface ManaAbility extends AbstractAbility, Optioned {

    /**
     * Gets the mana ability display name as defined in the locale's messages file.
     *
     * @param locale the locale to get the display name in
     * @return the display name in the specified locale or in a fallback language
     */
    String getDisplayName(Locale locale);

    String getDisplayName(Locale locale, boolean formatted);

    /**
     * Gets the mana ability description as defined in the locale's messages file.
     *
     * @param locale the locale to get the description in
     * @return the description in the specified locale or in a fallback language
     */
    String getDescription(Locale locale);

    String getDescription(Locale locale, boolean formatted);

    /**
     * Gets a fully uppercase String of the mana ability name without the namespace
     *
     * @return the mana ability name in all upper case
     */
    String name();

    /**
     * Gets whether the mana ability is enabled in the configuration. Mana abilities that are not loaded
     * in any skill will always return false. Disabled mana abilities should have no effect on gameplay.
     *
     * @return whether the mana ability is enabled
     */
    boolean isEnabled();

    /**
     * Gets the value of a mana ability at level 1 (just unlocked).
     *
     * @return the base value
     */
    double getBaseValue();

    /**
     * Gets the amount that the mana ability value is increased by for every mana ability level.
     * This value only starts being added from ability level 2. A negative value means
     * the mana ability value is decreased when leveled up.
     *
     * @return the change in value per mana ability level
     */
    double getValuePerLevel();

    /**
     * Gets the value of the mana ability at a specific mana ability level.
     *
     * @param level the mana ability level
     * @return the value at the level
     */
    double getValue(int level);

    /**
     * Gets the value of the mana ability displayed in menus at a specific mana ability level.
     * This is usually the same as {@link #getValue(int)}, but can be different if the value
     * has to be scaled to show an accurate value, such as multiplying by hp action bar scaling.
     *
     * @param level the mana ability level
     * @return the displayed value at the level
     */
    double getDisplayValue(int level);

    /**
     * Gets the base cooldown of the mana ability at level 1 (just unlocked).
     *
     * @return the base cooldown
     */
    double getBaseCooldown();

    /**
     * Gets the change in cooldown per mana ability level. Negative values mean
     * the cooldown decreases as the mana ability levels up.
     *
     * @return the change in cooldown per mana ability level
     */
    double getCooldownPerLevel();

    /**
     * Gets the cooldown at a specific mana ability level.
     *
     * @param level the mana ability level
     * @return the cooldown at the level
     */
    double getCooldown(int level);

    /**
     * Gets the base mana cost of the mana ability at level 1 (just unlocked).
     *
     * @return the base mana cost
     */
    double getBaseManaCost();

    /**
     * Gets the change in mana cost per mana ability level. Negative values
     * indicate a decrease in mana cost.
     *
     * @return the change in mana cost per mana ability level
     */
    double getManaCostPerLevel();

    /**
     * Gets the mana cost at a specific mana ability level.
     *
     * @param level the mana ability level
     * @return the mana cost at the level
     */
    double getManaCost(int level);

}
