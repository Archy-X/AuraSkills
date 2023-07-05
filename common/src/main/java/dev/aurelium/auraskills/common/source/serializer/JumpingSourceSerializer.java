package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.JumpingSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class JumpingSourceSerializer extends SourceSerializer<JumpingSource> {

    public JumpingSourceSerializer(AuraSkillsPlugin plugin, String sourceName) {
        super(plugin, sourceName);
    }

    @Override
    public JumpingSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        int interval = source.node("interval").getInt(100);

        return new JumpingSource(plugin, getId(), getXp(source), interval);
    }
}
