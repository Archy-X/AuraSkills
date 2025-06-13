package dev.aurelium.auraskills.bukkit.loot.type;

import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootValues;
import dev.aurelium.auraskills.bukkit.loot.item.ItemSupplier;

public class ItemLoot extends Loot {

    protected ItemSupplier item;
    protected int minAmount;
    protected int maxAmount;
    protected double minDamage;
    protected double maxDamage;

    public ItemLoot(LootValues values, ItemSupplier item, int minAmount, int maxAmount, double minDamage, double maxDamage) {
        super(values);
        this.item = item;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    public ItemSupplier getItem() {
        return item;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public double getMinDamage() {
        return minDamage;
    }

    public double getMaxDamage() {
        return maxDamage;
    }

}
