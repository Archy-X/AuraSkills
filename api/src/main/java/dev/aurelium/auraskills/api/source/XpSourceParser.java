package dev.aurelium.auraskills.api.source;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

@FunctionalInterface
public interface XpSourceParser<T> {

    T parse(ConfigurationNode source, SourceContext context) throws SerializationException;

}
