package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.EntitySource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class EntitySourceSerializer extends SourceSerializer<EntitySource> {

    public EntitySourceSerializer(AuraSkillsPlugin plugin, String sourceName) {
        super(plugin, sourceName);
    }

    @Override
    public EntitySource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        String entity = required(source, "entity").getString();
        EntityXpSource.EntityTriggers[] triggers = requiredPluralizedArray("trigger", source, EntityXpSource.EntityTriggers.class);
        EntityXpSource.EntityDamagers[] damagers = pluralizedArray("damager", source, EntityXpSource.EntityDamagers.class);

        return new EntitySource(plugin, getId(), getXp(source), getDisplayName(source), entity, triggers, damagers);
    }

}
