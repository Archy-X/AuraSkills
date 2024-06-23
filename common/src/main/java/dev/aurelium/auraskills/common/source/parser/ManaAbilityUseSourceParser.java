package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.ConfigurateSourceContext;
import dev.aurelium.auraskills.common.source.type.ManaAbilityUseSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class ManaAbilityUseSourceParser extends SourceParser<ManaAbilityUseSource> {

    public ManaAbilityUseSourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public ManaAbilityUseSource parse(ConfigurationNode source, ConfigurateSourceContext context) throws SerializationException {
        ManaAbility[] manaAbilities = context.pluralizedArray("mana_ability", source, ManaAbility.class);

        return new ManaAbilityUseSource(plugin, context.parseValues(source), manaAbilities);
    }
}
