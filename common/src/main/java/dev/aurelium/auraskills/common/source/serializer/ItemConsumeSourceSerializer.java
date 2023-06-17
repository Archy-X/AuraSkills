package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.common.source.type.ItemConsumeSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class ItemConsumeSourceSerializer extends SourceSerializer<ItemConsumeSource> {

    @Override
    public ItemConsumeSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        ItemFilter item = required(source, "item").get(ItemFilter.class);

        return new ItemConsumeSource(getId(source), getXp(source), item);
    }
}
