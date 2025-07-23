package dev.aurelium.auraskills.common.util.file;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

@FunctionalInterface
public interface ConfigUpdate {

    void apply(ConfigurationNode embedded, ConfigurationNode user) throws SerializationException;

}
