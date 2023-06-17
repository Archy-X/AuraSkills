package dev.auramc.auraskills.common.stat;

import dev.auramc.auraskills.api.stat.Stat;
import dev.auramc.auraskills.common.registry.Registry;

public class StatRegistry extends Registry<Stat> {

    public StatRegistry() {
        super(Stat.class);
    }

}
