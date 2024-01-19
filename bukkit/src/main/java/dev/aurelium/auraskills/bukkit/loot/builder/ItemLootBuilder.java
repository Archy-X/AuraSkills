package dev.aurelium.auraskills.bukkit.loot.builder;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.item.ItemSupplier;
import dev.aurelium.auraskills.bukkit.loot.type.ItemLoot;
import org.apache.commons.lang.Validate;

public class ItemLootBuilder extends LootBuilder {

    protected ItemSupplier item;
    protected int minAmount;
    protected int maxAmount;

    @Override
    public Loot build() {
        Validate.notNull(item, "You must specify an item");
        return new ItemLoot(weight, message, contexts, options, item, minAmount, maxAmount);
    }

    public ItemLootBuilder() {
        this.minAmount = 1;
        this.maxAmount = 1;
    }

    public ItemLootBuilder item(ItemSupplier item) {
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
