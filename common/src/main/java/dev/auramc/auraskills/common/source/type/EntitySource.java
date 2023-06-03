package dev.auramc.auraskills.common.source.type;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.EntityXpSource;
import dev.auramc.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;

public class EntitySource extends Source implements EntityXpSource {

    private final String entity;
    private final EntityTriggers[] triggers;
    private final EntityDamagers[] damagers;

    public EntitySource(NamespacedId id, double xp, String entity, EntityTriggers[] triggers, EntityDamagers[] damagers) {
        super(id, xp);
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
}
