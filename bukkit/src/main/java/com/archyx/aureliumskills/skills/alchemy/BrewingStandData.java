package com.archyx.aureliumskills.skills.alchemy;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BrewingStandData {

    private final Map<Integer, Boolean> potionSlots;
    private final ItemStack ingredient;

    public BrewingStandData(ItemStack ingredient) {
        this.potionSlots = new HashMap<>();
        this.ingredient = ingredient;
    }

    public boolean isSlotBrewed(int slot) {
        return potionSlots.getOrDefault(slot, false);
    }

    public void setSlotBrewed(int slot, boolean isSlotBrewed) {
        potionSlots.put(slot, isSlotBrewed);
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

}
