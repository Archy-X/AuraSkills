package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootParser;
import dev.aurelium.auraskills.api.loot.LootParsingContext;
import dev.aurelium.auraskills.bukkit.loot.LootManager;
import dev.aurelium.auraskills.bukkit.loot.entity.EntitySupplier;
import dev.aurelium.auraskills.bukkit.loot.type.EntityLoot;
import dev.aurelium.auraskills.common.util.data.Validate;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;


public class EntityLootParser implements LootParser {
    protected final LootManager manager;

    public EntityLootParser(LootManager manager) {
        this.manager = manager;
    }

    @Override
    public Loot parse(LootParsingContext context, ConfigurationNode config) throws SerializationException {
        String entityType = config.node("entity").getString();
        Validate.notNull(entityType, "Entity loot must specify an entity type");

        EntitySupplier entity = null;
        for (CustomEntityParser parser : manager.getCustomEntityParsers()) {
            if (parser.shouldUseParser(config)) {
                entity = parser.getEntitySupplier(config);
                break;
            }
        }

        Validate.notNull(entity, "Couldn't parse entity loot with entity type: " + entityType);

        return new EntityLoot(context.parseValues(config), entity);
    }
}
