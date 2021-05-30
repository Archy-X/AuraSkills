package com.archyx.aureliumskills.loot;

import java.util.List;

public class LootPool {

    private final String name;
    private final List<Loot> loot;
    private final double baseChance;
    private final int selectionPriority;

    public LootPool(String name, List<Loot> loot, double baseChance, int selectionPriority) {
        this.name = name;
        this.loot = loot;
        this.baseChance = baseChance;
        this.selectionPriority = selectionPriority;
    }

    public String getName() {
        return name;
    }

    public List<Loot> getLoot() {
        return loot;
    }

    public double getBaseChance() {
        return baseChance;
    }

    public int getSelectionPriority() {
        return selectionPriority;
    }

}
