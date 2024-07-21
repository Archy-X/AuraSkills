package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.source.type.DamageXpSource.DamageCause;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.ConfigurateSourceContext;
import dev.aurelium.auraskills.common.source.type.EntitySource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class EntitySourceParser extends SourceParser<EntitySource> {

    public EntitySourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public EntitySource parse(ConfigurationNode source, ConfigurateSourceContext context) throws SerializationException {
        String entity = context.required(source, "entity").getString();
        EntityXpSource.EntityTriggers[] triggers = context.requiredPluralizedArray("trigger", source, EntityXpSource.EntityTriggers.class);
        EntityXpSource.EntityDamagers[] damagers = context.pluralizedArray("damager", source, EntityXpSource.EntityDamagers.class);
        boolean scaleXpWithHealth = source.node("scale_xp_with_health").getBoolean(true);
        @Nullable DamageCause[] causes = context.pluralizedArray("cause", source, DamageCause.class);
        @Nullable DamageCause[] excludedCauses = context.pluralizedArray("excluded_cause", source, DamageCause.class);

        return new EntitySource(plugin, context.parseValues(source), entity, triggers, damagers, causes, excludedCauses, scaleXpWithHealth);
    }

}
