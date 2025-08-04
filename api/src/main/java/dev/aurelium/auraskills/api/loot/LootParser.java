package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.config.ConfigNode;

@FunctionalInterface
public interface LootParser {

    Loot parse(LootParsingContext context, ConfigNode config);

}
