package com.archyx.aureliumskills.skills.alchemy;

import java.util.HashMap;
import java.util.Map;

public class BrewingStandData {

    private final Map<Integer, Boolean> potionSlots;

    public BrewingStandData() {
        this.potionSlots = new HashMap<>();
    }

    public boolean isSlotBrewed(int slot) {
        return potionSlots.getOrDefault(slot, false);
    }

    public void setSlotBrewed(int slot, boolean isSlotBrewed) {
        potionSlots.put(slot, isSlotBrewed);
    }

}
