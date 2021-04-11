package com.archyx.aureliumskills.stats;

import org.bukkit.NamespacedKey;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StatRegistry {

    public final Map<NamespacedKey, Stat> stats;

    public StatRegistry() {
        this.stats = new HashMap<>();
    }

    public void register(NamespacedKey key, Stat stat) {
        this.stats.put(key, stat);
    }

    public Collection<Stat> getStats() {
        return stats.values();
    }

    public Stat getStat(NamespacedKey key) {
        return this.stats.get(key);
    }

}
