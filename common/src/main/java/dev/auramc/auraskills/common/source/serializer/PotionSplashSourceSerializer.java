package dev.auramc.auraskills.common.source.serializer;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.common.source.type.PotionSplashSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class PotionSplashSourceSerializer extends SourceSerializer<PotionSplashSource> {

    @Override
    public PotionSplashSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        ItemFilter item = required(source, "item").get(ItemFilter.class);

        return new PotionSplashSource(getId(source), getXp(source), item);
    }
}
