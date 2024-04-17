package dev.aurelium.auraskills.bukkit.loot.item;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.api.loot.LootTable;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantments;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ItemSupplier(
        ItemStack baseItem,
        @Nullable LootEnchantments enchantments
) {

    public ItemStack supplyItem(AuraSkills plugin, LootTable table) {
        ItemStack item = baseItem.clone();
        // Select and apply enchantments
        if (enchantments != null) {
            enchantments.applyEnchantments(item, plugin, table);
        }
        return item;
    }

}
