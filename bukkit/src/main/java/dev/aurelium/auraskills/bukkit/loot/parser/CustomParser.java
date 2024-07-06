package dev.aurelium.auraskills.bukkit.loot.parser;

import org.spongepowered.configurate.ConfigurationNode;

public interface CustomParser {

    boolean shouldUseParser(ConfigurationNode config);

}
