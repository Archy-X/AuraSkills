package dev.aurelium.auraskills.common.stat;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatProvider;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;

public class StatRegistry extends Registry<Stat> {

    public StatRegistry(AuraSkillsPlugin plugin) {
        super(plugin, Stat.class);
    }

    @Override
    public void registerDefaults() {
        for (Stat stat : Stats.values()) {
            injectProvider(stat, StatProvider.class, plugin.getStatManager().getSupplier());
            this.register(stat.getId(), stat);
        }
    }
}
