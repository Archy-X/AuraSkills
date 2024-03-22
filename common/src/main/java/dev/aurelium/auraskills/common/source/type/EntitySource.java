package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;

public class EntitySource extends Source implements EntityXpSource {

    private final String entity;
    private final EntityTriggers[] triggers;
    private final EntityDamagers[] damagers;
    private final boolean scaleXpWithHealth;

    public EntitySource(AuraSkillsPlugin plugin, SourceValues values, String entity, EntityTriggers[] triggers, EntityDamagers[] damagers, boolean scaleXpWithHealth) {
        super(plugin, values);
        this.entity = entity;
        this.triggers = triggers;
        this.damagers = damagers;
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
}
