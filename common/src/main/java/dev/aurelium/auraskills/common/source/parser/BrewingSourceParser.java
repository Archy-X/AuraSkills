package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.source.type.BrewingXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.ConfigurateSourceContext;
import dev.aurelium.auraskills.common.source.type.BrewingSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class BrewingSourceParser extends SourceParser<BrewingSource> {

    public BrewingSourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public BrewingSource parse(ConfigurationNode source, ConfigurateSourceContext context) throws SerializationException {
        ItemFilter ingredients = context.required(source, "ingredient").get(ItemFilter.class);
        BrewingXpSource.BrewTriggers trigger = context.required(source, "trigger").get(BrewingXpSource.BrewTriggers.class);

        return new BrewingSource(plugin, context.parseValues(source), ingredients, trigger);
    }
}
