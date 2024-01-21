package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.jetbrains.annotations.Nullable;

public class SourceValues {

    private final SourceType type;
    private final NamespacedId id;
    private final double xp;
    @Nullable
    private final String displayName;

    public SourceValues(SourceType type, NamespacedId id, double xp, @Nullable String displayName) {
        this.type = type;
        this.id = id;
        this.xp = xp;
        this.displayName = displayName;
    }

    public SourceType getType() {
        return type;
    }

    public NamespacedId getId() {
        return id;
    }


    public double getXp() {
        return xp;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }
}
