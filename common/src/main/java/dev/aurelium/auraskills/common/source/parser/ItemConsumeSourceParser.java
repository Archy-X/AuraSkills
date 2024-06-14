package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.ConfigurateSourceContext;
import dev.aurelium.auraskills.common.source.type.ItemConsumeSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class ItemConsumeSourceParser extends SourceParser<ItemConsumeSource> {

    public ItemConsumeSourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public ItemConsumeSource parse(ConfigurationNode source, ConfigurateSourceContext context) throws SerializationException {
        ItemFilter item = context.required(source, "item").get(ItemFilter.class);

        return new ItemConsumeSource(plugin, context.parseValues(source), item);
    }
}
