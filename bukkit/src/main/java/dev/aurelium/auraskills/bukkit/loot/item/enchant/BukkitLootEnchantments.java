package dev.aurelium.auraskills.bukkit.loot.item.enchant;

import dev.aurelium.auraskills.api.loot.LootTable;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.loot.enchant.LootEnchantEntry;
import dev.aurelium.auraskills.common.loot.enchant.LootEnchantList;
import dev.aurelium.auraskills.common.loot.enchant.LootEnchantments;
import dev.aurelium.auraskills.common.ref.ItemRef;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static dev.aurelium.auraskills.bukkit.ref.BukkitItemRef.unwrap;

public class BukkitLootEnchantments {

    private final LootEnchantments lootEnchantments;

    public BukkitLootEnchantments(LootEnchantments lootEnchantments) {
        this.lootEnchantments = lootEnchantments;
    }

    public void applyEnchantments(ItemRef ref, AuraSkills plugin, LootTable table) {
        ItemStack item = unwrap(ref);

        LootEnchantList list = selectEnchantList(); // Select the list of enchants to apply
        if (list == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        // Roll and apply enchants
        List<LeveledEnchant> enchants = rollEnchants(plugin, table, list);
        for (LeveledEnchant enchant : enchants) {
            if (meta instanceof EnchantmentStorageMeta esm) {
                esm.addStoredEnchant(enchant.enchant(), enchant.level(), true);
            } else {
                meta.addEnchant(enchant.enchant(), enchant.level(), true);
            }
        }
        item.setItemMeta(meta);
    }

    public List<LeveledEnchant> rollEnchants(AuraSkills plugin, LootTable table, LootEnchantList list) {
        List<LeveledEnchant> rolled = new ArrayList<>();
        for (LootEnchantEntry entry : list.entries()) {
            Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(entry.enchantName().toLowerCase(Locale.ROOT)));
            // Avoid getting a random double if the chance is 1.0
            double chance = 1.0;
            if (entry.chance() < 1.0) {
                chance = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
            }
            if (enchantment != null) {
                if (chance <= entry.chance()) {
                    int rolledLevel = ThreadLocalRandom.current().nextInt(entry.minLevel(), entry.maxLevel() + 1);
                    rolled.add(new LeveledEnchant(enchantment, rolledLevel));
                }
            } else {
                plugin.logger().warn("Error while rolling enchant in loot table " + table.getId() + ": " +
                        "Could not find enchantment in Minecraft registry with key " + entry.enchantName());
            }
        }
        return rolled;
    }

    @Nullable
    private LootEnchantList selectEnchantList() {
        if (lootEnchantments.possibleEnchants().isEmpty()) return null;

        int totalWeight = lootEnchantments.possibleEnchants().values().stream().reduce(Integer::sum).orElse(0);
        if (totalWeight == 0) {
            return null;
        }
        // Select a loot list randomly based on weight (the value of the possibleEnchants map)
        Random random = new Random();
        int selected = random.nextInt(totalWeight);
        int currentWeight = 0;
        LootEnchantList selectedList = null;
        for (LootEnchantList list : lootEnchantments.possibleEnchants().keySet()) {
            int listWeight = lootEnchantments.possibleEnchants().getOrDefault(list, 0);
            if (selected >= currentWeight && selected < currentWeight + listWeight) {
                selectedList = list;
                break;
            }
            currentWeight += listWeight;
        }
        return selectedList;
    }

}
