package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.type.GrindstoneXpSource;
import dev.aurelium.auraskills.common.source.Source;

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
