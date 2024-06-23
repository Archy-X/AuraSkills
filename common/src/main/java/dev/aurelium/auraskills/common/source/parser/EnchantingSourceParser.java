package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.ConfigurateSourceContext;
import dev.aurelium.auraskills.common.source.type.EnchantingSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class EnchantingSourceParser extends SourceParser<EnchantingSource> {

    public EnchantingSourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public EnchantingSource parse(ConfigurationNode source, ConfigurateSourceContext context) throws SerializationException {
        ItemFilter item = context.required(source, "item").get(ItemFilter.class);
        String unit = source.node("unit").getString();

        return new EnchantingSource(plugin, context.parseValues(source), item, unit);
    }
}
