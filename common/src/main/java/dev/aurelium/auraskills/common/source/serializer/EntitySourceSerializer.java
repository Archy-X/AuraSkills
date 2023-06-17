package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.common.source.type.EntitySource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class EntitySourceSerializer extends SourceSerializer<EntitySource> {

    @Override
    public EntitySource deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String entity = required(node, "entity").getString();
        EntityXpSource.EntityTriggers[] triggers = requiredPluralizedArray("trigger", node, EntityXpSource.EntityTriggers.class);
        EntityXpSource.EntityDamagers[] damagers = pluralizedArray("damager", node, EntityXpSource.EntityDamagers.class);

        return new EntitySource(getId(node), getXp(node), entity, triggers, damagers);
    }

}
