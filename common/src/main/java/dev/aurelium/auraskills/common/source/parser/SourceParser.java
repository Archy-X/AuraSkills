package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.source.SourceContext;
import dev.aurelium.auraskills.api.source.XpSourceParser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import dev.aurelium.auraskills.common.source.ConfigurateSourceContext;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public abstract class SourceParser<T> implements XpSourceParser<T> {

    protected final AuraSkillsPlugin plugin;

    public SourceParser(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract T parse(ConfigurationNode source, ConfigurateSourceContext context) throws SerializationException;

    @Override
    public T parse(ConfigNode source, SourceContext context) {
        try {
            return parse(((ApiConfigNode) source).getBacking(), new ConfigurateSourceContext(context));
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }
}
