package com.archyx.aureliumskills.loot;

import java.util.List;

public class LootPool {

    private final String name;
    private final List<LootEntry> loot;
    private final double baseChance;
    private final int selectionPriority;

    public LootPool(String name, List<LootEntry> loot, double baseChance, int selectionPriority) {
        this.name = name;
        this.loot = loot;
        this.baseChance = baseChance;
        this.selectionPriority = selectionPriority;
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

    public int getSelectionPriority() {
        return selectionPriority;
    }

}
