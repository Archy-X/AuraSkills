package dev.aurelium.auraskills.bukkit.loot.type;

import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootValues;
import dev.aurelium.auraskills.bukkit.loot.item.ItemSupplier;

public class ItemLoot extends Loot {

    protected ItemSupplier item;
    protected int minAmount;
    protected int maxAmount;

    public ItemLoot(LootValues values, ItemSupplier item, int minAmount, int maxAmount) {
        super(values);
        this.item = item;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
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

}
