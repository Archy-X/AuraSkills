package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.source.type.DamageXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.ConfigurateSourceContext;
import dev.aurelium.auraskills.common.source.type.DamageSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class DamageSourceParser extends SourceParser<DamageSource> {

    public DamageSourceParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DamageSource parse(ConfigurationNode source, ConfigurateSourceContext context) throws SerializationException {
        DamageXpSource.DamageCause[] causes = context.pluralizedArray("cause", source, DamageXpSource.DamageCause.class);
        DamageXpSource.DamageCause[] excludedCauses = context.pluralizedArray("excluded_cause", source, DamageXpSource.DamageCause.class);
        String[] damagers = context.pluralizedArray("damager", source, String.class);
        String[] excludedDamagers = context.pluralizedArray("excluded_damager", source, String.class);
        boolean mustSurvive = source.node("must_survive").getBoolean(true);
        boolean useOriginalDamage = source.node("use_original_damage").getBoolean(true);
        boolean includeProjectiles = source.node("include_projectiles").getBoolean(true);
        int cooldownMsg = source.node("cooldown_ms").getInt(200);

        return new DamageSource(plugin, context.parseValues(source), causes, excludedCauses, damagers, excludedDamagers, mustSurvive, useOriginalDamage, includeProjectiles, cooldownMsg);
    }
}
