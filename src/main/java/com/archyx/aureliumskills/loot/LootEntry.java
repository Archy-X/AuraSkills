package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.AureliumSkills;

public abstract class LootEntry {

    protected final AureliumSkills plugin;
    private final int weight;

    public LootEntry(AureliumSkills plugin, int weight) {
        this.plugin = plugin;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

}
