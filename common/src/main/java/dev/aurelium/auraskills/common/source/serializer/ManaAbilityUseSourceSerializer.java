package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.ManaAbilityUseSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class ManaAbilityUseSourceSerializer extends SourceSerializer<ManaAbilityUseSource> {

    public ManaAbilityUseSourceSerializer(AuraSkillsPlugin plugin, String sourceName) {
        super(plugin, sourceName);
    }

    @Override
    public ManaAbilityUseSource deserialize(Type type, ConfigurationNode node) throws SerializationException {
        ManaAbility[] manaAbilities = pluralizedArray("mana_ability", node, ManaAbility.class);

        return new ManaAbilityUseSource(plugin, getId(), getXp(node), manaAbilities);
    }
}
