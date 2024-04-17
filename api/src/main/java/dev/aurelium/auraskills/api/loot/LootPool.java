package dev.aurelium.auraskills.api.loot;

import java.util.*;
import java.util.stream.Collectors;

public class LootPool extends LootOptioned {

    private final String name;
    private final List<Loot> loot;
    private final double baseChance;
    private final int selectionPriority;
    private final boolean overrideVanillaLoot;
    private final Random random = new Random();

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

    /**
     * Rolls a {@link Loot} entry from the pool's loot list randomly based on each loot's weight.
     * The chance of an entry being selected is the weight/totalWeight. Specific loot entries can be
     * filtered out before selection using the LootContextFilter.
     *
     * @param filter a filter checked on each loot entry, if the filter returns false the loot entry is ignored.
     * @return the selected Loot as an optional
     */
    public Optional<Loot> rollLoot(LootContextFilter filter) {
        List<Loot> lootList = new ArrayList<>(loot).stream()
                .filter(filter::passesFilter)
                .collect(Collectors.toList());

        int totalWeight = 0;
        for (Loot loot : lootList) {
            totalWeight += loot.getValues().getWeight();
        }
        if (totalWeight == 0) { // Don't attempt selection if no loot entries are applicable
            return Optional.empty();
        }
        int selected = random.nextInt(totalWeight);
        int currentWeight = 0;
        Loot selectedLoot = null;
        for (Loot loot : lootList) {
            if (selected >= currentWeight && selected < currentWeight + loot.getValues().getWeight()) {
                selectedLoot = loot;
                break;
            }
            currentWeight += loot.getValues().getWeight();
        }
        return Optional.ofNullable(selectedLoot);
    }

}
