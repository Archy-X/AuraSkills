package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;

import java.util.Locale;

public class Source implements XpSource {

    private final NamespacedId id;
    private final double xp;

    public Source(NamespacedId id, double xp) {
        this.id = id;
        this.xp = xp;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public String name() {
        return id.getKey().toUpperCase(Locale.ROOT);
    }

    @Override
    public double getXp() {
        return xp;
    }
}
