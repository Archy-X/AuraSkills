package dev.auramc.auraskills.common.source.serializer;

import dev.auramc.auraskills.api.item.PotionData;
import dev.auramc.auraskills.common.item.SourcePotionData;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class PotionDataSerializer extends SourceSerializer<PotionData> {

    @Override
    public PotionData deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String[] types = pluralizedArray("type", source, String.class);
        String[] excludedTypes = pluralizedArray("excluded_type", source, String.class);
        boolean extended = source.node("extended").getBoolean(false);
        boolean upgraded = source.node("upgraded").getBoolean(false);

        return new SourcePotionData(types, excludedTypes, extended, upgraded);
    }
}
