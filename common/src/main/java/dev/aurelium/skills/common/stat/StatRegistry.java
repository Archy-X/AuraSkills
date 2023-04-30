package dev.aurelium.skills.common.stat;

import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.api.stat.StatProperties;
import dev.aurelium.skills.api.stat.Stats;
import dev.aurelium.skills.common.registry.Registry;

public class StatRegistry extends Registry<Stat, StatProperties> {

    public StatRegistry(Class<Stat> type, Class<StatProperties> propertyType) {
        super(type, propertyType);
    }

    @Override
    public void registerDefaults() {
        for (Stats stat : Stats.values()) {
            StatProperties properties = new DefaultStat(stat);
            register(stat.getId(), stat, properties);
        }
    }
}
