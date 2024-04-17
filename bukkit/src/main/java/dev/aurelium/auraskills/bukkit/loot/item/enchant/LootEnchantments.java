package dev.aurelium.auraskills.bukkit.loot.item.enchant;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.api.loot.LootTable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Random;

public record LootEnchantments(Map<LootEnchantList, Integer> possibleEnchants) {

    public void applyEnchantments(ItemStack item, AuraSkills plugin, LootTable table) {
        LootEnchantList list = selectEnchantList(); // Select the list of enchants to apply
        if (list == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        // Roll and apply enchants
        List<LeveledEnchant> enchants = list.rollEnchants(plugin, table);
        for (LeveledEnchant enchant : enchants) {
            if (meta instanceof EnchantmentStorageMeta esm) {
                esm.addStoredEnchant(enchant.enchant(), enchant.level(), true);
            } else {
                meta.addEnchant(enchant.enchant(), enchant.level(), true);
            }
        }
        item.setItemMeta(meta);
    }

    @Nullable
    private LootEnchantList selectEnchantList() {
        if (possibleEnchants.isEmpty()) return null;

        int totalWeight = possibleEnchants.values().stream().reduce(Integer::sum).orElse(0);
        if (totalWeight == 0) {
            return null;
        }
        // Select a loot list randomly based on weight (the value of the possibleEnchants map)
        Random random = new Random();
        int selected = random.nextInt(totalWeight);
        int currentWeight = 0;
        LootEnchantList selectedList = null;
        for (LootEnchantList list : possibleEnchants.keySet()) {
            int listWeight = possibleEnchants.getOrDefault(list, 0);
            if (selected >= currentWeight && selected < currentWeight + listWeight) {
                selectedList = list;
                break;
            }
            currentWeight += listWeight;
        }
        return selectedList;
    }

}
