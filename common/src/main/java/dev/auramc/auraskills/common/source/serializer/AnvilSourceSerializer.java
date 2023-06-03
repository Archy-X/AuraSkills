package dev.auramc.auraskills.common.source.serializer;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.common.source.type.AnvilSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class AnvilSourceSerializer extends SourceSerializer<AnvilSource> {

    @Override
    public AnvilSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        ItemFilter leftItem = required(source, "left_item").get(ItemFilter.class);
        ItemFilter rightItem = required(source, "right_item").get(ItemFilter.class);
        String multiplier = source.node("multiplier").getString();

        return new AnvilSource(getId(source), getXp(source), leftItem, rightItem, multiplier);
    }
}
