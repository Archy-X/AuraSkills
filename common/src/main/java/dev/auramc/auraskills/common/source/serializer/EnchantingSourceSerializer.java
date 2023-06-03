package dev.auramc.auraskills.common.source.serializer;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.common.source.type.EnchantingSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class EnchantingSourceSerializer extends SourceSerializer<EnchantingSource> {

    @Override
    public EnchantingSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        ItemFilter item = required(source, "item").get(ItemFilter.class);

        return new EnchantingSource(getId(source), getXp(source), item);
    }
}
