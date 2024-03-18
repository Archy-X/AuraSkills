package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;

public interface SourceType {

    /**
     * Gets the {@link NamespacedId} identifying the source type.
     *
     * @return the id
     */
    NamespacedId getId();

    /**
     * Gets the parser used to deserialize sources from configuration.
     *
     * @return the parser
     */
    XpSourceParser<?> getParser();

    /**
     * Gets whether at least one instance of the source type is defined and loaded from
     * the plugin's configuration.
     *
     * @return whether the source is enabled
     */
    boolean isEnabled();

}
