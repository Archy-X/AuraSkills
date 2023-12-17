package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.ItemConsumeSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class ItemConsumeSourceSerializer extends SourceSerializer<ItemConsumeSource> {

    public ItemConsumeSourceSerializer(AuraSkillsPlugin plugin, String sourceName) {
        super(plugin, sourceName);
    }

    @Override
    public ItemConsumeSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        ItemFilter item = required(source, "item").get(ItemFilter.class);

        return new ItemConsumeSource(plugin, getId(), getXp(source), getDisplayName(source), item);
    }
}
