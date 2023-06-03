package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.type.GrindstoneSource;

public class GrindstoneSourceBuilder extends SourceBuilder {

    private String multiplier;

    public GrindstoneSourceBuilder(NamespacedId id) {
        super(id);
    }

    public GrindstoneSourceBuilder multiplier(String multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new GrindstoneSource(id, xp, multiplier);
    }
}
