package dev.aurelium.auraskills.bukkit.hooks.mythicmobs.loot;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.entity.EntityProperties;
import dev.aurelium.auraskills.bukkit.loot.entity.EntitySupplier;
import dev.aurelium.auraskills.bukkit.loot.parser.CustomEntityParser;
import org.spongepowered.configurate.ConfigurationNode;

public class MythicEntityLootParser implements CustomEntityParser {

    private final AuraSkills plugin;

    public MythicEntityLootParser(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public EntitySupplier getEntitySupplier(ConfigurationNode config) {
        return new MythicEntitySupplier(EntityProperties.fromConfig(config, plugin));
    }

    @Override
    public boolean shouldUseParser(ConfigurationNode config) {
        return config.node("entity").getString("").startsWith("mythicmobs:");
    }
}
