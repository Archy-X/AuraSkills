package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.source.type.DamageXpSource.DamageCause;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntitySource extends Source implements EntityXpSource {

    private final String entity;
    private final EntityTriggers[] triggers;
    private final EntityDamagers[] damagers;
    private final DamageCause[] causes;
    private final DamageCause[] excludedCauses;
    private final boolean scaleXpWithHealth;

    public EntitySource(AuraSkillsPlugin plugin, SourceValues values, String entity, EntityTriggers[] triggers, EntityDamagers[] damagers,
                        @Nullable DamageCause[] causes, @Nullable DamageCause[] excludedCauses, boolean scaleXpWithHealth) {
        super(plugin, values);
        this.entity = entity;
        this.triggers = triggers;
        this.damagers = damagers;
        this.causes = causes;
        this.excludedCauses = excludedCauses;
        this.scaleXpWithHealth = scaleXpWithHealth;
    }

    @Override
    public @NotNull String getEntity() {
        return entity;
    }

    @Override
    public @NotNull EntityTriggers[] getTriggers() {
        return triggers;
    }

    @Override
    public EntityDamagers[] getDamagers() {
        return damagers;
    }

    @Override
    public boolean isVersionValid() {
        return plugin.getPlatformUtil().isValidEntityType(entity);
    }

    @Override
    public boolean scaleXpWithHealth() {
        return scaleXpWithHealth;
    }

    @Override
    @Nullable
    public DamageCause[] getCauses() {
        return causes;
    }

    @Override
    @Nullable
    public DamageCause[] getExcludedCauses() {
        return excludedCauses;
    }
}
