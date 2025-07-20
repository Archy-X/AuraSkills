package dev.aurelium.auraskills.common.loot;

import org.spongepowered.configurate.ConfigurationNode;

public interface CustomItemParser extends CustomParser {

    ItemSupplier parseCustomItem(ConfigurationNode config);

}
