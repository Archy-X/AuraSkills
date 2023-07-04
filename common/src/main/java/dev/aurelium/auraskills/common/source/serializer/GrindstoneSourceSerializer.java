package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.GrindstoneSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class GrindstoneSourceSerializer extends SourceSerializer<GrindstoneSource> {

    public GrindstoneSourceSerializer(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public GrindstoneSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String multiplier = source.node("multiplier").getString();

        return new GrindstoneSource(plugin, getId(source), getXp(source), multiplier);
    }
}
