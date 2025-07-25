package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.registry.NamespacedId;

@FunctionalInterface
public interface LootParser {

    Loot parse(NamespacedId id, LootParsingContext context, ConfigNode config);

}
