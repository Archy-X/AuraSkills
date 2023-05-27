package dev.auramc.auraskills.api.source;

import dev.auramc.auraskills.api.registry.NamespacedId;

import java.util.Locale;

public interface XpSource {

    /**
     * Gets the id of the source. Any source set in the plugin config will use the auraskills namespace.
     * Repeated sources in different skills will have the same NamespacedId but are different instances.
     *
     * @return The id
     */
    NamespacedId getId();

    /**
     * Gets the display name of the source.
     *
     * @param locale The locale to get the display name in
     * @return The display name
     */
    String getDisplayName(Locale locale);

    /**
     * Gets the name of the source in all caps without a namespace.
     * Different sources may return the same name.
     *
     * @return The name in all caps
     */
    String name();

    /**
     * Gets the config section used to get the message of the source.
     * This is usually the name of the default skill the source is in,
     * but fighting and archery sources share the 'mobs' section.
     * Custom sources will use the 'custom' section.
     *
     * @return The config section
     */
    String getMessageSection();

    /**
     * Gets the amount of xp the source gives.
     * The value is the base amount before any multipliers are applied.
     *
     * @return The base xp of the source
     */
    int getXp();

}
