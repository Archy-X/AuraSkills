package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.common.ref.ItemRef;
import org.spongepowered.configurate.ConfigurationNode;

public interface CustomItemParser extends CustomParser {

    ItemRef parseCustomItem(ConfigurationNode config);

}
