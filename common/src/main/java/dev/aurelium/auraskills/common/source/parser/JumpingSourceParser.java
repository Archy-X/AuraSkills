package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.source.SourceContext;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.JumpingSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class JumpingSourceParser extends SourceParser<JumpingSource> {

    public JumpingSourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public JumpingSource parse(ConfigurationNode source, SourceContext context) throws SerializationException {
        int interval = source.node("interval").getInt(100);

        return new JumpingSource(plugin, context.parseValues(source), interval);
    }
}
