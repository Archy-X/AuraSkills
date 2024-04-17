package dev.aurelium.auraskills.api.stat;

import dev.aurelium.auraskills.api.option.Optioned;
import dev.aurelium.auraskills.api.registry.NamespaceIdentified;
import dev.aurelium.auraskills.api.trait.Trait;

import java.util.List;
import java.util.Locale;

public interface Stat extends Optioned, NamespaceIdentified {

    /**
     * Gets whether the stat is enabled in the configuration. Disabled stats
     * have no effect and are hidden from menus and messages.
     *
     * @return whether the stat is enabled
     */
    boolean isEnabled();

    /**
     * Gets the list of {@link Trait} instances leveled by the stat. Traits are the gameplay aspects buffed
     * by a stat. The trait value is determined by the stat level multiplied by the modifier
     * in stats.yml.
     *
     * @return the list of traits
     */
    List<Trait> getTraits();

    /**
     * Gets the modifier value for a {@link Trait} associated with the stat defined in stats.yml.
     * The modifier value is multiplied by the stat level to determine the trait value, which is the
     * value directly used by the trait implementation.
     *
     * @param trait the trait modifier value
     * @return the trait modifier
     */
    double getTraitModifier(Trait trait);

    /**
     * Gets the stat display name as defined in the locale's messages file.
     *
     * @param locale the locale to get the display name in
     * @return the display name in the specified locale or in a fallback language
     */
    String getDisplayName(Locale locale);

    /**
     * Gets the stat display name as defined in the locale's messages file.
     *
     * @param locale the locale to get the display name in
     * @param formatted whether to apply formatting to the display name
     * @return the display name in the specified locale or in a fallback language
     */
    String getDisplayName(Locale locale, boolean formatted);

    /**
     * Gets the stat description as defined in the locale's messages file.
     *
     * @param locale the locale to get the description in
     * @return the description in the specified locale or in a fallback language
     */
    String getDescription(Locale locale);

    /**
     * Gets the stat description as defined in the locale's messages file.
     *
     * @param locale the locale to get the description in
     * @param formatted whether to apply formatting to the description
     * @return the description in the specified locale or in a fallback language
     */
    String getDescription(Locale locale, boolean formatted);

    /**
     * Gets the stat color as defined in the messages file.
     *
     * @param locale the locale to get the color in
     * @return the color in the specified locale or in a fallback language
     */
    String getColor(Locale locale);

    /**
     * Gets the stat's display name applied with its color
     *
     * @param locale the locale to get the name and color in
     * @return the colored name
     */
    String getColoredName(Locale locale);

    /**
     * Gets the symbol as defined in the messages file.
     *
     * @param locale the locale to get the symbol in
     * @return the symbol in the specified locale or in a fallback language
     */
    String getSymbol(Locale locale);

    /**
     * Gets a fully uppercase String of the stat name without the namespace
     *
     * @return the stat name in all upper case
     */
    String name();

}
