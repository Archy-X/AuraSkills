package dev.auramc.auraskills.common.source.serializer;

import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.common.source.type.ManaAbilityUseSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class ManaAbilityUseSourceSerializer extends SourceSerializer<ManaAbilityUseSource> {

    @Override
    public ManaAbilityUseSource deserialize(Type type, ConfigurationNode node) throws SerializationException {
        ManaAbility[] manaAbilities = pluralizedArray("mana_ability", node, ManaAbility.class);

        return new ManaAbilityUseSource(getId(node), getXp(node), manaAbilities);
    }
}
