package com.archyx.aureliumskills.loot.builder;

import com.archyx.aureliumskills.AureliumSkills;
import org.bukkit.inventory.ItemStack;

public abstract class ItemLootBuilder extends LootBuilder {

    protected ItemStack item;
    protected int minAmount;
    protected int maxAmount;

    public ItemLootBuilder(AureliumSkills plugin) {
        super(plugin);
        this.minAmount = 1;
        this.maxAmount = 1;
    }

    public ItemLootBuilder item(ItemStack item) {
        this.item = item;
        return this;
    }

    public ItemLootBuilder minAmount(int minAmount) {
        this.minAmount = minAmount;
        return this;
    }

    public ItemLootBuilder maxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
        return this;
    }

}
