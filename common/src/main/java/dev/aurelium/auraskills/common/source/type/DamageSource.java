package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.source.type.DamageXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.Nullable;

public class DamageSource extends Source implements DamageXpSource {

    private final DamageCause[] causes;
    private final DamageCause[] excludedCauses;
    private final @Nullable String[] damagers;
    private final @Nullable String[] excludedDamagers;
    private final boolean mustSurvive;
    private final boolean useOriginalDamage;
    private final boolean includeProjectiles;
    private final int cooldownMs;

    public DamageSource(AuraSkillsPlugin plugin, SourceValues values, DamageCause[] causes, DamageCause[] excludedCauses,
                        String[] damagers, String[] excludedDamagers, boolean mustSurvive, boolean useOriginalDamage, boolean includeProjectiles, int cooldownMs) {
        super(plugin, values);
        this.causes = causes;
        this.excludedCauses = excludedCauses;
        this.damagers = damagers;
        this.excludedDamagers = excludedDamagers;
        this.mustSurvive = mustSurvive;
        this.useOriginalDamage = useOriginalDamage;
        this.includeProjectiles = includeProjectiles;
        this.cooldownMs = cooldownMs;
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
        return damagers == null ? null : damagers[0];
    }

    @Override
    public @Nullable String[] getDamagers() {
        return damagers;
    }

    @Override
    public @Nullable String[] getExcludedDamagers() {
        return excludedDamagers;
    }

    @Override
    public boolean mustSurvive() {
        return mustSurvive;
    }

    @Override
    public boolean useOriginalDamage() {
        return useOriginalDamage;
    }

    @Override
    public boolean includeProjectiles() {
        return includeProjectiles;
    }

    @Override
    public int getCooldownMs() {
        return cooldownMs;
    }
}
