package dev.aurelium.auraskills.common.stat;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.registry.Registry;

public class StatRegistry extends Registry<Stat> {

    public StatRegistry() {
        super(Stat.class);
    }

}
