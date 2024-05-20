package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.option.Optioned;
import dev.aurelium.auraskills.api.registry.NamespaceIdentified;

import java.util.Locale;

public interface Trait extends Optioned, NamespaceIdentified {

    /**
     * Gets whether the trait is enabled. Disabled traits will have no effect
     * and will be hidden from menus.
     *
     * @return whether the trait is enabled
     */
    boolean isEnabled();

    /**
     * Gets the trait display name as defined in the locale's messages file.
     *
     * @param locale the locale to get the display name
     * @return the display name in the specified locale or a fallback language
     */
    String getDisplayName(Locale locale);

    /**
     * Gets the trait display name as defined in the locale's messages file.
     *
     * @param locale the locale to get the display name
     * @param formatted whether to apply formatting to the display name
     * @return the display name in the specified locale or a fallback language
     */
    String getDisplayName(Locale locale, boolean formatted);

    /**
     * Formats the given value of this trait to the format shown in the stats menu.
     *
     * @param value the value of the trait
     * @param locale the locale to format in
     * @return the formatted value
     */
    String getMenuDisplay(double value, Locale locale);

    /**
     * Gets a fully uppercase String of the trait name without the namespace
     *
     * @return the trait name in all upper case
     */
    String name();

}
