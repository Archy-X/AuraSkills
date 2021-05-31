package com.archyx.aureliumskills.loot.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;

public abstract class LootBuilder {

    protected final AureliumSkills plugin;
    protected int weight;
    protected String message;

    public LootBuilder(AureliumSkills plugin) {
        this.plugin = plugin;
        this.weight = 10;
        this.message = "";
    }

    public LootBuilder weight(int weight) {
        this.weight = weight;
        return this;
    }

    public LootBuilder message(String message) {
        this.message = message;
        return this;
    }

    public abstract Loot build();

}
