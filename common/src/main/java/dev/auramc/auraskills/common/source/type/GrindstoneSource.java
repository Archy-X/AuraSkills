package dev.auramc.auraskills.common.source.type;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.GrindstoneXpSource;
import dev.auramc.auraskills.common.source.Source;

public class GrindstoneSource extends Source implements GrindstoneXpSource {

    private final String multiplier;

    public GrindstoneSource(NamespacedId id, double xp, String multiplier) {
        super(id, xp);
        this.multiplier = multiplier;
    }

    @Override
    public String getMultiplier() {
        return multiplier;
    }
}
