package dev.aurelium.auraskills.api.source;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

@FunctionalInterface
public interface UtilityParser<T> {

    T parse(ConfigurationNode source, BaseContext context) throws SerializationException;

}
