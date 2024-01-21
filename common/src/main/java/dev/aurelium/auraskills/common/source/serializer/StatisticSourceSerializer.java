package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.StatisticSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class StatisticSourceSerializer extends SourceSerializer<StatisticSource> {

    public StatisticSourceSerializer(AuraSkillsPlugin plugin, SourceType sourceType, String sourceName) {
        super(plugin, sourceType, sourceName);
    }

    @Override
    public StatisticSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String statistic = required(source, "statistic").getString();
        double multiplier = source.node("multiplier").getDouble(1.0);
        int minimumIncrease = source.node("minimum_increase").getInt(1);

        return new StatisticSource(plugin, parseValues(source), statistic, multiplier, minimumIncrease);
    }
}
