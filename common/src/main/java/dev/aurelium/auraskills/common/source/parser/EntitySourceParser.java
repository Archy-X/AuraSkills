package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.source.SourceContext;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.EntitySource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class EntitySourceParser extends SourceParser<EntitySource> {

    public EntitySourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public EntitySource parse(ConfigurationNode source, SourceContext context) throws SerializationException {
        String entity = context.required(source, "entity").getString();
        EntityXpSource.EntityTriggers[] triggers = context.requiredPluralizedArray("trigger", source, EntityXpSource.EntityTriggers.class);
        EntityXpSource.EntityDamagers[] damagers = context.pluralizedArray("damager", source, EntityXpSource.EntityDamagers.class);
        boolean scaleXpWithHealth = source.node("scale_xp_with_health").getBoolean(true);

        return new EntitySource(plugin, context.parseValues(source), entity, triggers, damagers, scaleXpWithHealth);
    }

}
