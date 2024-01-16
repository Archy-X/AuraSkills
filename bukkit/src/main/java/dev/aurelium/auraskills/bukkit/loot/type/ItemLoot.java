package dev.aurelium.auraskills.bukkit.loot.type;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.context.LootContext;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class ItemLoot extends Loot {

    protected ItemStack item;
    protected int minAmount;
    protected int maxAmount;

    public ItemLoot(int weight, String message, Map<String, Set<LootContext>> contexts, Map<String, Object> options,
                    ItemStack item, int minAmount, int maxAmount) {
        super(weight, message, contexts, options);
        this.item = item;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

}
