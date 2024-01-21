package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.ManaAbilityUseSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class ManaAbilityUseSourceSerializer extends SourceSerializer<ManaAbilityUseSource> {

    public ManaAbilityUseSourceSerializer(AuraSkillsPlugin plugin, SourceType sourceType, String sourceName) {
        super(plugin, sourceType, sourceName);
    }

    @Override
    public ManaAbilityUseSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        ManaAbility[] manaAbilities = pluralizedArray("mana_ability", source, ManaAbility.class);

        return new ManaAbilityUseSource(plugin, parseValues(source), manaAbilities);
    }
}
