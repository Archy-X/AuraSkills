package dev.aurelium.skills.common.stat;

import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.api.util.NamespacedId;

public class DefaultStat implements Stat {

    private final NamespacedId id;

    public DefaultStat(NamespacedId id) {
        this.id = id;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }
}
