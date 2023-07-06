package dev.aurelium.auraskills.common.source.serializer.util;

import dev.aurelium.auraskills.api.item.ItemCategory;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.item.LootItemFilter;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.item.LootSourceItem;
import dev.aurelium.auraskills.common.source.serializer.SourceSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class LootItemFilterSerializer extends SourceSerializer<LootItemFilter> {

    public LootItemFilterSerializer(AuraSkillsPlugin plugin, String sourceName) {
        super(plugin, sourceName);
    }

    @Override
    public LootItemFilter deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String[] materials = pluralizedArray("material", source, String.class);
        String[] excludedMaterials = pluralizedArray("excluded_material", source, String.class);
        ItemCategory category = source.node("category").get(ItemCategory.class);
        String lootTable = source.node("loot_table").getString();
        ItemFilterMeta meta = new ItemFilterMetaSerializer(plugin, sourceName).deserialize(ItemFilterMeta.class, source);

        return new LootSourceItem(materials, excludedMaterials, category, meta, lootTable);
    }
}
