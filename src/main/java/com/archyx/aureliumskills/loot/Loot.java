package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.AureliumSkills;

public abstract class Loot {

    protected final AureliumSkills plugin;
    private final int weight;

    public Loot(AureliumSkills plugin, int weight) {
        this.plugin = plugin;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

}
