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
     * Gets the name of the source type in all lowercase.
     *
     * @return the source type name
     */
    String getName();

    Class<? extends XpSource> getSourceClass();

    Class<? extends XpSourceSerializer<?>> getSerializerClass();

}
