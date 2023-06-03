package dev.auramc.auraskills.common.source.type;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.JumpingXpSource;
import dev.auramc.auraskills.common.source.Source;

public class JumpingSource extends Source implements JumpingXpSource {

    private final int interval;

    public JumpingSource(NamespacedId id, double xp, int interval) {
        super(id, xp);
        this.interval = interval;
    }

    @Override
    public int getInterval() {
        return interval;
    }
}
