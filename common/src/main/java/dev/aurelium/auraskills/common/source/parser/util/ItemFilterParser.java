package dev.aurelium.auraskills.common.source.parser.util;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.item.ItemCategory;
import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.source.BaseContext;
import dev.aurelium.auraskills.api.source.UtilityParser;
import dev.aurelium.auraskills.common.item.SourceItem;

public class ItemFilterParser implements UtilityParser<ItemFilter> {

    @Override
    public ItemFilter parse(ConfigNode source, BaseContext context) {
        if (!source.isMap() && !source.isList()) {
            String material = source.getString();
            if (material == null) {
                throw new RuntimeException("Invalid direct String value item filter, must be of type String");
            }
            return new SourceItem(new String[]{material}, null, null, null);
        }
        String[] materials = context.pluralizedArray("material", source, String.class);
        String[] excludedMaterials = context.pluralizedArray("excluded_material", source, String.class);
        ItemCategory category = source.node("category").get(ItemCategory.class);
        ItemFilterMeta meta = new ItemFilterMetaParser().parse(source, context);

        return new SourceItem(materials, excludedMaterials, category, meta);
    }
}
