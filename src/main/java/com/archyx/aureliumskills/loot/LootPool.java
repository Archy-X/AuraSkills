package com.archyx.aureliumskills.loot;

import java.util.List;

public class LootPool {

    private final String name;
    private final List<LootEntry> loot;
    private final double baseChance;

    public LootPool(String name, List<LootEntry> loot, double baseChance) {
        this.name = name;
        this.loot = loot;
        this.baseChance = baseChance;
    }

    public String getName() {
        return name;
    }

    public List<LootEntry> getLoot() {
        return loot;
    }

    public double getBaseChance() {
        return baseChance;
    }

}
