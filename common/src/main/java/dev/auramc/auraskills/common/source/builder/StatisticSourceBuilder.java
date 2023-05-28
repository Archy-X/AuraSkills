package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.annotation.Required;
import dev.auramc.auraskills.common.source.type.StatisticSource;

public class StatisticSourceBuilder extends SourceBuilder {

    private @Required String statistic;
    private double multiplier = 1.0;

    public StatisticSourceBuilder(NamespacedId id) {
        super(id);
    }

    public StatisticSourceBuilder statistic(String statistic) {
        this.statistic = statistic;
        return this;
    }

    public StatisticSourceBuilder multiplier(double multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new StatisticSource(id, displayName, xp, statistic, multiplier);
    }
}
