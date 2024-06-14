package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.ConfigurateSourceContext;
import dev.aurelium.auraskills.common.source.type.GrindstoneSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class GrindstoneSourceParser extends SourceParser<GrindstoneSource> {

    public GrindstoneSourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public GrindstoneSource parse(ConfigurationNode source, ConfigurateSourceContext context) throws SerializationException {
        String multiplier = source.node("multiplier").getString();

        return new GrindstoneSource(plugin, context.parseValues(source), multiplier);
    }
}
