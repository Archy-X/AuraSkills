package dev.aurelium.auraskills.common.source.serializer.util;

import dev.aurelium.auraskills.api.item.ItemCategory;
import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.item.SourceItem;
import dev.aurelium.auraskills.common.source.serializer.SourceSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class ItemFilterSerializer extends SourceSerializer<ItemFilter> {

    public ItemFilterSerializer(AuraSkillsPlugin plugin, String sourceName) {
        super(plugin, sourceName);
    }

    @Override
    public ItemFilter deserialize(Type type, ConfigurationNode source) throws SerializationException {
        if (!source.isMap() && !source.isList()) {
            String material = source.getString();
            if (material == null) {
                throw new SerializationException("Invalid direct String value item filter for source " + sourceName);
            }
            return new SourceItem(new String[]{material}, null, null, null);
        }
        String[] materials = pluralizedArray("material", source, String.class);
        String[] excludedMaterials = pluralizedArray("excluded_material", source, String.class);
        ItemCategory category = source.node("category").get(ItemCategory.class);
        ItemFilterMeta meta = new ItemFilterMetaSerializer(plugin, sourceName).deserialize(ItemFilterMeta.class, source);

        return new SourceItem(materials, excludedMaterials, category, meta);
    }
}
