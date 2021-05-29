package com.archyx.aureliumskills.loot.type;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.LootEntry;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ItemLoot extends LootEntry {

    protected ItemStack item;
    protected int minAmount;
    protected int maxAmount;

    public ItemLoot(AureliumSkills plugin, ItemStack item, int minAmount, int maxAmount) {
        super(plugin);
        this.item = item;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    protected int generateAmount() {
        return new Random().nextInt(maxAmount - minAmount + 1) + minAmount;
    }

}
