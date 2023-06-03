package dev.auramc.auraskills.common.source.serializer;

import dev.auramc.auraskills.api.item.ItemCategory;
import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.item.ItemFilterMeta;
import dev.auramc.auraskills.common.item.SourceItem;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class ItemFilterSerializer extends SourceSerializer<ItemFilter> {

    @Override
    public ItemFilter deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String[] materials = pluralizedArray("material", source, String.class);
        String[] excludedMaterials = pluralizedArray("excluded_material", source, String.class);
        ItemCategory category = source.node("category").get(ItemCategory.class);
        ItemFilterMeta meta = new ItemFilterMetaSerializer().deserialize(ItemFilterMeta.class, source);

        return new SourceItem(materials, excludedMaterials, category, meta);
    }
}
