package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.EntityXpSource;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.annotation.Required;
import dev.auramc.auraskills.common.source.type.EntitySource;

public class EntitySourceBuilder extends SourceBuilder {

    private @Required String entity;
    private @Required EntityXpSource.EntityTriggers[] triggers;
    private EntityXpSource.EntityDamagers[] damagers;

    public EntitySourceBuilder(NamespacedId id) {
        super(id);
    }

    public EntitySourceBuilder entity(String entity) {
        this.entity = entity;
        return this;
    }

    public EntitySourceBuilder triggers(EntityXpSource.EntityTriggers... triggers) {
        this.triggers = triggers;
        return this;
    }

    public EntitySourceBuilder damagers(EntityXpSource.EntityDamagers... damagers) {
        this.damagers = damagers;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new EntitySource(id, displayName, xp, entity, triggers, damagers);
    }
}
