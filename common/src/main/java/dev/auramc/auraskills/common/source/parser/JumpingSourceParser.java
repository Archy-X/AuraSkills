package dev.auramc.auraskills.common.source.parser;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.source.builder.JumpingSourceBuilder;
import org.spongepowered.configurate.ConfigurationNode;

public class JumpingSourceParser extends SourceParser<JumpingSourceBuilder> {

    public JumpingSourceParser() {
        super(new JumpingSourceBuilder(NamespacedId.from(NamespacedId.AURASKILLS, "jumping")));
    }

    @Override
    public void parse(ConfigurationNode section) {
        parseXp(section);

        builder.interval(section.node("interval").getInt(100));
    }
}
