package dev.auramc.auraskills.common.source;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.XpSource;

import java.util.Locale;

public class Source implements XpSource {

    private final NamespacedId id;
    private final String displayName;
    private final double xp;

    public Source(NamespacedId id, String displayName, double xp) {
        this.id = id;
        this.displayName = displayName;
        this.xp = xp;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public String getDisplayName(Locale locale) {
        return displayName;
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
