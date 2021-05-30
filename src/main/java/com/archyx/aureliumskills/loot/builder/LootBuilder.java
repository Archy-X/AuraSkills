package com.archyx.aureliumskills.loot.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.LootEntry;

public abstract class LootBuilder {

    protected final AureliumSkills plugin;
    protected int weight;

    public LootBuilder(AureliumSkills plugin) {
        this.plugin = plugin;
        this.weight = 10;
    }

    public LootBuilder weight(int weight) {
        this.weight = weight;
        return this;
    }

    public abstract LootEntry build();

}
