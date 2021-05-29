package com.archyx.aureliumskills.loot.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.LootEntry;

public abstract class LootBuilder {

    protected final AureliumSkills plugin;

    public LootBuilder(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public abstract LootEntry build();

}
