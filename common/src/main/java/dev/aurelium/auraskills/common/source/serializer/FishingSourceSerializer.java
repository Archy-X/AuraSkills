package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.item.LootItemFilter;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.FishingSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class FishingSourceSerializer extends SourceSerializer<FishingSource> {

    public FishingSourceSerializer(AuraSkillsPlugin plugin, SourceType sourceType, String sourceName) {
        super(plugin, sourceType, sourceName);
    }

    @Override
    public FishingSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        LootItemFilter item = required(source, "item").get(LootItemFilter.class);

        return new FishingSource(plugin, parseValues(source), item);
    }
}
