package dev.auramc.auraskills.common.source.serializer;

import dev.auramc.auraskills.api.item.ItemFilterMeta;
import dev.auramc.auraskills.api.item.PotionData;
import dev.auramc.auraskills.common.item.SourceItemMeta;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.List;

public class ItemFilterMetaSerializer extends SourceSerializer<ItemFilterMeta> {

    @Override
    public ItemFilterMeta deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String displayName = source.node("display_name").getString();
        List<String> lore = source.node("lore").getList(String.class);
        // Deserialize PotionData
        PotionData potionData = new PotionDataSerializer().deserialize(PotionData.class, source.node("potion_data"));

        return new SourceItemMeta(displayName, lore, potionData);
    }

}
