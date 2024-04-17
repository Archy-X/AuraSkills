package dev.aurelium.auraskills.api.loot;

import org.spongepowered.configurate.ConfigurationNode;

public interface LootParsingContext {

    LootValues parseValues(ConfigurationNode config);

}
