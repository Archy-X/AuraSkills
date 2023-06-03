package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.type.JumpingSource;

public class JumpingSourceBuilder extends SourceBuilder {

    private int interval;

    public JumpingSourceBuilder(NamespacedId id) {
        super(id);
    }

    public JumpingSourceBuilder interval(int interval) {
        this.interval = interval;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new JumpingSource(id, xp, interval);
    }
}
