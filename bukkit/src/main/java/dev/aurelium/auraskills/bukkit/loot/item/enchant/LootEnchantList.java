package dev.aurelium.auraskills.bukkit.loot.item.enchant;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.api.loot.LootTable;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public record LootEnchantList(List<LootEnchantEntry> entries) {

    public List<LeveledEnchant> rollEnchants(AuraSkills plugin, LootTable table) {
        List<LeveledEnchant> rolled = new ArrayList<>();
        for (LootEnchantEntry entry : entries) {
            Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(entry.enchantName().toLowerCase(Locale.ROOT)));
            if (enchantment != null) {
                int rolledLevel = ThreadLocalRandom.current().nextInt(entry.minLevel(), entry.maxLevel() + 1);
                rolled.add(new LeveledEnchant(enchantment, rolledLevel));
            } else {
                plugin.logger().warn("Error while rolling enchant in loot table " + table.getId() + ": " +
                        "Could not find enchantment in Minecraft registry with key " + entry.enchantName());
            }
        }
        return rolled;
    }

}
