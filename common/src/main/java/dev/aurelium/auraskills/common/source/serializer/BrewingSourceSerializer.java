package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.api.source.type.BrewingXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.BrewingSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class BrewingSourceSerializer extends SourceSerializer<BrewingSource> {

    public BrewingSourceSerializer(AuraSkillsPlugin plugin, SourceType sourceType, String sourceName) {
        super(plugin, sourceType, sourceName);
    }

    @Override
    public BrewingSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        ItemFilter ingredients = required(source, "ingredient").get(ItemFilter.class);
        BrewingXpSource.BrewTriggers trigger = required(source, "trigger").get(BrewingXpSource.BrewTriggers.class);

        return new BrewingSource(plugin, parseValues(source), ingredients, trigger);
    }
}
