package dev.aurelium.auraskills.common.source.parser.util;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.item.PotionData;
import dev.aurelium.auraskills.api.source.BaseContext;
import dev.aurelium.auraskills.api.source.UtilityParser;
import dev.aurelium.auraskills.common.item.SourceItemMeta;

import java.util.List;

public class ItemFilterMetaParser implements UtilityParser<ItemFilterMeta> {

    @Override
    public ItemFilterMeta parse(ConfigNode source, BaseContext context) {
        String displayName = source.node("display_name").getString();
        List<String> lore = source.node("lore").getList(String.class);
        // Deserialize PotionData
        PotionData potionData;
        if (!source.node("potion_data").virtual()) {
            potionData = new PotionDataParser().parse(source.node("potion_data"), context);
        } else {
            potionData = null;
        }
        boolean hasCustomModelData = source.node("has_custom_model_data").getBoolean(false);
        if (!source.node("custom_model_data").virtual()) {
            hasCustomModelData = true;
        }
        int customModelData = source.node("custom_model_data").getInt(Integer.MIN_VALUE);

        return new SourceItemMeta(displayName, lore, potionData, hasCustomModelData, customModelData);
    }

}
