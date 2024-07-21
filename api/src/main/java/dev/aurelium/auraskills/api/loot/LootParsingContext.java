package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.config.ConfigNode;

public interface LootParsingContext {

    LootValues parseValues(ConfigNode config);

}
