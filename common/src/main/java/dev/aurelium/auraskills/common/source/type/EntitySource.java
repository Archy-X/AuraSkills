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

    public EntitySource(AuraSkillsPlugin plugin, SourceValues values, String entity, EntityTriggers[] triggers, EntityDamagers[] damagers) {
        super(plugin, values);
        this.entity = entity;
        this.triggers = triggers;
        this.damagers = damagers;
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
}
