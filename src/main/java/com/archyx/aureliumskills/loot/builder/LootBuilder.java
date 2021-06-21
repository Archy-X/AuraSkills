package com.archyx.aureliumskills.loot.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;

public abstract class LootBuilder {

    protected final AureliumSkills plugin;
    protected int weight;
    protected String message;
    protected double xp;

    public LootBuilder(AureliumSkills plugin) {
        this.plugin = plugin;
        this.weight = 10;
        this.message = "";
        this.xp = -1;
    }

    public LootBuilder weight(int weight) {
        this.weight = weight;
        return this;
    }

    public LootBuilder message(String message) {
        this.message = message;
        return this;
    }

    public LootBuilder xp(double xp) {
        this.xp = xp;
        return this;
    }

    public abstract Loot build();

}
