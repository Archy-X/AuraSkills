package dev.aurelium.auraskills.common.source.serializer.util;

import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.item.PotionData;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.item.SourceItemMeta;
import dev.aurelium.auraskills.common.source.serializer.SourceSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.List;

public class ItemFilterMetaSerializer extends SourceSerializer<ItemFilterMeta> {

    public ItemFilterMetaSerializer(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public ItemFilterMeta deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String displayName = source.node("display_name").getString();
        List<String> lore = source.node("lore").getList(String.class);
        // Deserialize PotionData
        PotionData potionData = new PotionDataSerializer(plugin).deserialize(PotionData.class, source.node("potion_data"));

        return new SourceItemMeta(displayName, lore, potionData);
    }

}
