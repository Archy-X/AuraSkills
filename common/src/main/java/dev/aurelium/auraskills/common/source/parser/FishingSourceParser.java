package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.item.LootItemFilter;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.ConfigurateSourceContext;
import dev.aurelium.auraskills.common.source.type.FishingSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class FishingSourceParser extends SourceParser<FishingSource> {

    public FishingSourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public FishingSource parse(ConfigurationNode source, ConfigurateSourceContext context) throws SerializationException {
        LootItemFilter item = context.required(source, "item").get(LootItemFilter.class);

        return new FishingSource(plugin, context.parseValues(source), item);
    }
}
