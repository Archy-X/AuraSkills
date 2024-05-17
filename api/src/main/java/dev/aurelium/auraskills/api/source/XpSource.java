package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface XpSource {

    /**
     * Gets the id of the source. Any source set in the plugin config will use the auraskills namespace.
     * Repeated sources in different skills will have the same NamespacedId but are different instances.
     *
     * @return The id
     */
    NamespacedId getId();

    SourceType getType();

    /**
     * Gets the display name of the source.
     * Different sources may return the same name.
     *
     * @param locale The locale to get the name in
     * @return The display name
     */
    String getDisplayName(Locale locale);

    @Nullable
    String getUnitName(Locale locale);

    /**
     * Gets the name of the source in all caps without a namespace.
     * Different sources may return the same name.
     *
     * @return The name in all caps
     */
    String name();
    /**
     * Gets the amount of xp the source gives.
     * The value is the base amount before any multipliers are applied.
     *
     * @return The base xp of the source
     */
    double getXp();

    /**
     * Gets the income data for this source that determines the money gained
     * when the source is gained. Income is only gained if jobs are enabled.
     *
     * @return the source income data
     */
    SourceIncome getIncome();

    /**
     * Gets the {@link SourceValues} object that contains data about the source. All the information
     * in the source values is already accessible through other methods in this interface.
     *
     * @return the source values
     */
    SourceValues getValues();

    /**
     * Checks if the XP source is valid on the server's Minecraft version.
     *
     * @return whether the XP source is valid
     */
    default boolean isVersionValid() {
        return true;
    }

}
