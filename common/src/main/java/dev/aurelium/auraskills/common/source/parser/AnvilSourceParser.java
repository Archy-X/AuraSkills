package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.source.SourceContext;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.AnvilSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class AnvilSourceParser extends SourceParser<AnvilSource> {

    public AnvilSourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public AnvilSource parse(ConfigurationNode source, SourceContext context) throws SerializationException {
        ItemFilter leftItem = context.required(source, "left_item").get(ItemFilter.class);
        ItemFilter rightItem = context.required(source, "right_item").get(ItemFilter.class);
        String multiplier = source.node("multiplier").getString();

        return new AnvilSource(plugin, context.parseValues(source), leftItem, rightItem, multiplier);
    }
}
