package com.archyx.aureliumskills.loot.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.source.Source;

import java.util.ArrayList;
import java.util.List;

public abstract class LootBuilder {

    protected final AureliumSkills plugin;
    protected int weight;
    protected String message;
    protected double xp;
    protected List<Source> sources;

    public LootBuilder(AureliumSkills plugin) {
        this.plugin = plugin;
        this.weight = 10;
        this.message = "";
        this.xp = -1.0;
        this.sources = new ArrayList<>();
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

    public LootBuilder sources(List<Source> sources) {
        this.sources = sources;
        return this;
    }

    public abstract Loot build();

}
