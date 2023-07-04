package dev.aurelium.auraskills.common.source.serializer.util;

import dev.aurelium.auraskills.api.item.PotionData;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.item.SourcePotionData;
import dev.aurelium.auraskills.common.source.serializer.SourceSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class PotionDataSerializer extends SourceSerializer<PotionData> {

    public PotionDataSerializer(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public PotionData deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String[] types = pluralizedArray("type", source, String.class);
        String[] excludedTypes = pluralizedArray("excluded_type", source, String.class);
        boolean extended = source.node("extended").getBoolean(false);
        boolean upgraded = source.node("upgraded").getBoolean(false);

        return new SourcePotionData(types, excludedTypes, extended, upgraded);
    }
}
