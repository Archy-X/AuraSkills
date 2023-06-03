package dev.auramc.auraskills.common.source.parser;

import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.builder.SourceBuilder;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class SourceParser<T extends SourceBuilder> {

    protected final T builder;

    public SourceParser(T builder) {
        this.builder = builder;
    }

    public abstract void parse(ConfigurationNode section);

    protected void parseXp(ConfigurationNode section) {
        builder.xp(section.node("xp").getDouble());
    }

    public Source build() {
        return builder.build();
    }

}
