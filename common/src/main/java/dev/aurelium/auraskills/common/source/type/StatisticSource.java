package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.type.StatisticXpSource;
import dev.aurelium.auraskills.common.source.Source;

public class StatisticSource extends Source implements StatisticXpSource {

    private final String statistic;
    private final double multiplier;

    public StatisticSource(NamespacedId id, double xp, String statistic, double multiplier) {
        super(id, xp);
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
