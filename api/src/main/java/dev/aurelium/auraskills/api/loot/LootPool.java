package dev.aurelium.auraskills.api.loot;

import java.util.List;
import java.util.Map;

public class LootPool extends LootOptioned {

    private final String name;
    private final List<Loot> loot;
    private final double baseChance;
    private final int selectionPriority;
    private final boolean overrideVanillaLoot;

    public LootPool(String name, List<Loot> loot, double baseChance, int selectionPriority, boolean overrideVanillaLoot, Map<String, Object> options) {
        super(options);
        this.name = name;
        this.loot = loot;
        this.baseChance = baseChance;
        this.selectionPriority = selectionPriority;
        this.overrideVanillaLoot = overrideVanillaLoot;
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

    public boolean overridesVanillaLoot() {
        return overrideVanillaLoot;
    }

}
