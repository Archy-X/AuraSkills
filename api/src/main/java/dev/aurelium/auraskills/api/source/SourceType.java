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

}
