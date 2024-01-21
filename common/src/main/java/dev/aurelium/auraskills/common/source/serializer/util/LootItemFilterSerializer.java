package dev.aurelium.auraskills.common.source.serializer.util;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.item.ItemCategory;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.item.LootItemFilter;
import dev.aurelium.auraskills.common.item.LootSourceItem;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class LootItemFilterSerializer extends UtilitySerializer<LootItemFilter> {

    public LootItemFilterSerializer(AuraSkillsApi api) {
        super(api);
    }

    @Override
    public LootItemFilter deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String[] materials = pluralizedArray("material", source, String.class);
        String[] excludedMaterials = pluralizedArray("excluded_material", source, String.class);
        ItemCategory category = source.node("category").get(ItemCategory.class);
        String lootPool = source.node("loot_pool").getString();
        ItemFilterMeta meta = new ItemFilterMetaSerializer(getApi()).deserialize(ItemFilterMeta.class, source);

        return new LootSourceItem(materials, excludedMaterials, category, meta, lootPool);
    }
}
