package dev.aurelium.auraskills.bukkit.loot.parser;

import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

public interface CustomItemParser extends CustomParser {

    ItemStack parseCustomItem(ConfigurationNode config);

}
