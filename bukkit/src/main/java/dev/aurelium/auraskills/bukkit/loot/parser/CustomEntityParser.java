package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.bukkit.loot.entity.EntitySupplier;
import dev.aurelium.auraskills.common.loot.CustomParser;
import org.spongepowered.configurate.ConfigurationNode;

public interface CustomEntityParser extends CustomParser {

    EntitySupplier getEntitySupplier(ConfigurationNode config);

}
