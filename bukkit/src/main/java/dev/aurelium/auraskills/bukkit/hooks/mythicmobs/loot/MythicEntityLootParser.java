package dev.aurelium.auraskills.bukkit.hooks.mythicmobs.loot;

import dev.aurelium.auraskills.bukkit.loot.entity.EntityProperties;
import dev.aurelium.auraskills.bukkit.loot.entity.EntitySupplier;
import dev.aurelium.auraskills.bukkit.loot.parser.CustomEntityParser;
import org.spongepowered.configurate.ConfigurationNode;

public class MythicEntityLootParser implements CustomEntityParser {

    @Override
    public EntitySupplier getEntitySupplier(ConfigurationNode config) {
        return new MythicEntitySupplier(EntityProperties.fromConfig(config));
    }

    @Override
    public boolean shouldUseParser(ConfigurationNode config) {
        return config.node("entity").getString().startsWith("mythicmobs:");
    }
}
