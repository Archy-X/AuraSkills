package dev.aurelium.skills.common.stat;

import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.api.stat.StatProperties;
import org.jetbrains.annotations.NotNull;

public class DefaultStat implements StatProperties {

    private final Stat stat;

    public DefaultStat(Stat stat) {
        this.stat = stat;
    }

    @NotNull
    @Override
    public Stat getStat() {
        return stat;
    }
}
