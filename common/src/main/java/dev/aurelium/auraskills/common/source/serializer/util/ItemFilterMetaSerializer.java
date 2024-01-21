package dev.aurelium.auraskills.common.source.serializer.util;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.item.PotionData;
import dev.aurelium.auraskills.common.item.SourceItemMeta;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.List;

public class ItemFilterMetaSerializer extends UtilitySerializer<ItemFilterMeta> {

    public ItemFilterMetaSerializer(AuraSkillsApi api) {
        super(api);
    }

    @Override
    public ItemFilterMeta deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String displayName = source.node("display_name").getString();
        List<String> lore = source.node("lore").getList(String.class);
        // Deserialize PotionData
        PotionData potionData;
        if (!source.node("potion_data").virtual()) {
            potionData = new PotionDataSerializer(getApi()).deserialize(PotionData.class, source.node("potion_data"));
        } else {
            potionData = null;
        }

        return new SourceItemMeta(displayName, lore, potionData);
    }

}
