package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootParser;
import dev.aurelium.auraskills.api.loot.LootParsingContext;
import dev.aurelium.auraskills.bukkit.loot.LootManager;
import dev.aurelium.auraskills.bukkit.loot.entity.EntitySupplier;
import dev.aurelium.auraskills.bukkit.loot.type.EntityLoot;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import dev.aurelium.auraskills.common.util.data.Validate;
import org.spongepowered.configurate.ConfigurationNode;

public class EntityLootParser implements LootParser {

    protected final LootManager manager;

    public EntityLootParser(LootManager manager) {
        this.manager = manager;
    }

    @Override
    public Loot parse(LootParsingContext context, ConfigNode config) {
        ConfigurationNode backing = ((ApiConfigNode) config).getBacking();

        String entityType = backing.node("entity").getString();
        Validate.notNull(entityType, "Entity loot must specify an entity type");

        EntitySupplier entity = null;
        for (CustomEntityParser parser : manager.getCustomEntityParsers()) {
            if (parser.shouldUseParser(backing)) {
                entity = parser.getEntitySupplier(backing);
                break;
            }
        }

        Validate.notNull(entity, "Couldn't parse entity loot with entity type: " + entityType);

        return new EntityLoot(context.parseValues(config), entity);
    }
}
