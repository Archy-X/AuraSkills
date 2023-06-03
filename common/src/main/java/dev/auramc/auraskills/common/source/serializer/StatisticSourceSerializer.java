package dev.auramc.auraskills.common.source.serializer;

import dev.auramc.auraskills.common.source.type.StatisticSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class StatisticSourceSerializer extends SourceSerializer<StatisticSource> {

    @Override
    public StatisticSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String statistic = required(source, "statistic").getString();
        double multiplier = source.node("multiplier").getDouble(1.0);

        return new StatisticSource(getId(source), getXp(source), statistic, multiplier);
    }
}
