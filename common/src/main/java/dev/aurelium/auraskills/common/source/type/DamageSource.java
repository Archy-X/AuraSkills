package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.type.DamageXpSource;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.Nullable;

public class DamageSource extends Source implements DamageXpSource {

    private final DamageCause[] causes;
    private final DamageCause[] excludedCauses;
    private final String damager;
    private final boolean mustSurvive;
    private final boolean useOriginalDamage;

    public DamageSource(NamespacedId id, double xp, DamageCause[] causes, DamageCause[] excludedCauses, String damager, boolean mustSurvive, boolean useOriginalDamage) {
        super(id, xp);
        this.causes = causes;
        this.excludedCauses = excludedCauses;
        this.damager = damager;
        this.mustSurvive = mustSurvive;
        this.useOriginalDamage = useOriginalDamage;
    }

    @Override
    public @Nullable DamageCause[] getCauses() {
        return causes;
    }

    @Override
    public @Nullable DamageCause[] getExcludedCauses() {
        return excludedCauses;
    }

    @Override
    public @Nullable String getDamager() {
        return damager;
    }

    @Override
    public boolean mustSurvive() {
        return mustSurvive;
    }

    @Override
    public boolean useOriginalDamage() {
        return useOriginalDamage;
    }
}
