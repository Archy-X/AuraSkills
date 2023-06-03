package dev.auramc.auraskills.common.source.serializer;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.source.type.BrewingXpSource;
import dev.auramc.auraskills.common.source.type.BrewingSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class BrewingSourceSerializer extends SourceSerializer<BrewingSource> {

    @Override
    public BrewingSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        ItemFilter ingredients = required(source, "ingredients").get(ItemFilter.class);
        BrewingXpSource.BrewTriggers[] triggers = requiredPluralizedArray("trigger", source, BrewingXpSource.BrewTriggers.class);

        return new BrewingSource(getId(source), getXp(source), ingredients, triggers);
    }
}
