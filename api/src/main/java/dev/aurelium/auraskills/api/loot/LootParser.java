package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.config.ConfigNode;

import java.util.List;

@FunctionalInterface
public interface LootParser {

    Loot parse(LootParsingContext context, ConfigNode config, List<ConfigNode> requirements);

}
