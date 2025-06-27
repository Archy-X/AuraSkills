package dev.aurelium.auraskills.bukkit.loot.item;

import dev.aurelium.auraskills.api.loot.LootTable;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantments;
import dev.aurelium.auraskills.common.ref.ItemRef;
import org.jetbrains.annotations.Nullable;

public record ItemSupplier(
        ItemRef baseItem,
        @Nullable LootEnchantments enchantments
) {

    public ItemRef supplyItem(AuraSkills plugin, LootTable table) {
        ItemRef item = baseItem.clone();
        // Select and apply enchantments
        if (enchantments != null) {
            enchantments.applyEnchantments(item, plugin, table);
        }
        return item;
    }

}
