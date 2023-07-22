package dev.aurelium.auraskills.api.source.type;

import dev.aurelium.auraskills.api.source.XpSource;

public interface StatisticXpSource extends XpSource {

    /**
     * Gets the name of the statistic of the source.
     *
     * @return The statistic name
     */
    String getStatistic();

    /**
     * Gets the multiplier used to calculate the xp of the source.
     * This is used to allow the xp value to be in a more readable format.
     * For example, a per_cm statistic would have a multiplier of 0.01 for the configured xp to be in meters.
     *
     * @return The multiplier
     */
    double getMultiplier();

    /**
     * Gets the minimum value the statistic must increase by for the source to give xp.
     * If the statistic increases by less than this value, it will still be accounted for in
     * the next time XP is given.
     *
     * @return The minimum increase
     */
    int getMinimumIncrease();

}
