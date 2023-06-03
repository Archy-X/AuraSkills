package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.DamageXpSource;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.type.DamageSource;

public class DamageSourceBuilder extends SourceBuilder {

    private DamageXpSource.DamageCause[] causes;
    private DamageXpSource.DamageCause[] excludedCauses;
    private String damager;
    private boolean mustSurvive = true;
    private boolean useOriginalDamage = true;

    public DamageSourceBuilder(NamespacedId id) {
        super(id);
    }

    public DamageSourceBuilder causes(DamageXpSource.DamageCause... causes) {
        this.causes = causes;
        return this;
    }

    public DamageSourceBuilder excludedCauses(DamageXpSource.DamageCause... excludedCauses) {
        this.excludedCauses = excludedCauses;
        return this;
    }

    public DamageSourceBuilder damager(String damager) {
        this.damager = damager;
        return this;
    }

    public DamageSourceBuilder mustSurvive(boolean mustSurvive) {
        this.mustSurvive = mustSurvive;
        return this;
    }

    public DamageSourceBuilder useOriginalDamage(boolean useOriginalDamage) {
        this.useOriginalDamage = useOriginalDamage;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new DamageSource(id, xp, causes, excludedCauses, damager, mustSurvive, useOriginalDamage);
    }
}
