package dev.auramc.auraskills.common.source.type;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.StatisticXpSource;
import dev.auramc.auraskills.common.source.Source;

public class StatisticSource extends Source implements StatisticXpSource {

    private final String statistic;
    private final double multiplier;

    public StatisticSource(NamespacedId id, String displayName, double xp, String statistic, double multiplier) {
        super(id, displayName, xp);
        this.statistic = statistic;
        this.multiplier = multiplier;
    }

    @Override
    public String getStatistic() {
        return statistic;
    }

    @Override
    public double getMultiplier() {
        return multiplier;
    }
}
