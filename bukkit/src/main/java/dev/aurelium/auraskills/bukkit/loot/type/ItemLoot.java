package dev.aurelium.auraskills.bukkit.loot.type;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.context.LootContext;
import dev.aurelium.auraskills.bukkit.loot.item.ItemSupplier;

import java.util.Map;
import java.util.Set;

public class ItemLoot extends Loot {

    protected ItemSupplier item;
    protected int minAmount;
    protected int maxAmount;

    public ItemLoot(int weight, String message, Map<String, Set<LootContext>> contexts, Map<String, Object> options,
                    ItemSupplier item, int minAmount, int maxAmount) {
        super(weight, message, contexts, options);
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
