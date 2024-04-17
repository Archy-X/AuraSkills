package dev.aurelium.auraskills.api.loot;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

@FunctionalInterface
public interface LootParser {

    Loot parse(LootParsingContext context, ConfigurationNode config) throws SerializationException;

}
