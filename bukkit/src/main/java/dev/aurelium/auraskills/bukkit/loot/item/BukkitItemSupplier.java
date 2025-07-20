package dev.aurelium.auraskills.bukkit.loot.item;

import dev.aurelium.auraskills.api.loot.LootTable;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.BukkitLootEnchantments;
import dev.aurelium.auraskills.common.loot.ItemSupplier;
import dev.aurelium.auraskills.common.ref.ItemRef;
import org.jetbrains.annotations.Nullable;

import static dev.aurelium.auraskills.bukkit.ref.BukkitItemRef.wrap;

public class BukkitItemSupplier {

    private final ItemSupplier supplier;

    public BukkitItemSupplier(ItemSupplier supplier) {
        this.supplier = supplier;
    }

    @Nullable
    public ItemRef supplyItem(AuraSkills plugin, LootTable table) {
        ItemRef baseItem = supplier.baseItem();
        ItemRef item = null;
        if (baseItem != null) {
            item = supplier.baseItem().clone();
        }
        if (supplier.baseItemKey() != null) {
            item = wrap(plugin.getItemRegistry().getItem(supplier.baseItemKey()));
        }
        // Select and apply enchantments
        if (supplier.enchantments() != null) {
            new BukkitLootEnchantments(supplier.enchantments()).applyEnchantments(item, plugin, table);
        }
        return item;
    }

}
