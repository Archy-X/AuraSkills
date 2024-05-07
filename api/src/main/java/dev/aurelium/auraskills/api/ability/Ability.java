package dev.aurelium.auraskills.api.ability;

import dev.aurelium.auraskills.api.option.Optioned;

import java.util.Locale;

public interface Ability extends AbstractAbility, Optioned {

    /**
     * Gets the ability display name as defined in the locale's messages file.
     *
     * @param locale the locale to get the display name in
     * @return the display name in the specified locale or in a fallback language
     */
    String getDisplayName(Locale locale);

    String getDisplayName(Locale locale, boolean formatted);

    /**
     * Gets the ability description as defined in the locale's messages file.
     *
     * @param locale the locale to get the description in
     * @return the description in the specified locale or in a fallback language
     */
    String getDescription(Locale locale);

    String getDescription(Locale locale, boolean formatted);

    /**
     * Gets the ability info text as defined in the locale's messages file.
     * The info text shows an ability's effects for a given level in a concise way
     * and is shown in the main skills menu.
     *
     * @param locale the locale to get the info in
     * @return the info text in the specified locale or in a fallback language
     */
    String getInfo(Locale locale);

    String getInfo(Locale locale, boolean formatted);

    /**
     * Gets a fully uppercase String of the ability name without the namespace
     *
     * @return the ability name in all upper case
     */
    String name();

    /**
     * Gets whether the ability has a secondary value that varies with the ability level
     *
     * @return whether the ability has a secondary value
     */
    boolean hasSecondaryValue();

    /**
     * Gets whether the ability is enabled in the configuration. Abilities that are not loaded
     * in any skill will always return false. Disabled abilities should have no effect on gameplay.
     *
     * @return whether the ability is enabled
     */
    boolean isEnabled();

    /**
     * Gets the value of an ability when it is at level 1 (just unlocked).
     *
     * @return the base value
     */
    double getBaseValue();

    /**
     * Gets the secondary value of an ability when it is at level 1 (just unlocked).
     * If {@link #hasSecondaryValue()} returns false, this method will return 0.
     *
     * @return the secondary base value
     */
    double getSecondaryBaseValue();

    /**
     * Gets the value of the ability at a specific ability level.
     *
     * @param level the ability level
     * @return the value at the level
     */
    double getValue(int level);

    /**
     * Gets the amount that the ability value is increased by for every ability level.
     * This value only starts being added from ability level 2. A negative value means
     * the ability value is decreased when leveled up.
     *
     * @return the change in value per ability level
     */
    double getValuePerLevel();

    /**
     * Gets the amount that the secondary ability value is increased by for every ability level.
     * This value only starts being added from ability level 2. A negative value means
     * the ability value is decreased when leveled up.
     * If {@link #hasSecondaryValue()} returns false, this method will return 0.
     *
     * @return the change in secondary value per ability level
     */
    double getSecondaryValuePerLevel();

    /**
     * Gets the secondary value of the ability at a specific ability level.
     * If {@link #hasSecondaryValue()} returns false, this method will return 0.
     *
     * @param level the ability level
     * @return the secondary value at the level
     */
    double getSecondaryValue(int level);

}
