package com.archyx.aureliumskills.loot.parser;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.builder.ItemLootBuilder;
import com.archyx.aureliumskills.util.misc.Validate;
import com.cryptomorin.xseries.XEnchantment;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class ItemLootParser extends LootParser {

    public ItemLootParser(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Loot parse(Map<?, ?> map) {
        ItemStack item = parseItem(map);
        Validate.notNull(item, "Failed to parse item");

        int[] amount = parseAmount(map);

        return new ItemLootBuilder(plugin).item(item)
                .minAmount(amount[0])
                .maxAmount(amount[1])
                .message(getMessage(map))
                .weight(getWeight(map)).build();
    }
    
    private ItemStack parseItem(Map<?, ?> map) {
        // Parse material
        String itemString = getString(map, "item");
        Material material = Material.valueOf(itemString.toUpperCase(Locale.ROOT));
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        // Display name and lore
        if (map.containsKey("display_name")) {
            String displayName = getString(map, "display_name");
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        if (map.containsKey("lore")) {
            List<String> lore = getStringList(map, "lore");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        // Enchantments
        if (map.containsKey("enchantments")) {
            List<String> enchantmentStrings = getStringList(map, "enchantments");
            for (String enchantmentEntry : enchantmentStrings) {
                String[] splitEntry = enchantmentEntry.split(" ");
                String enchantmentName = splitEntry[0];
                int level = 1;
                if (splitEntry.length > 1) {
                    level = NumberUtils.toInt(splitEntry[1], 1);
                }
                Optional<XEnchantment> xEnchantment = XEnchantment.matchXEnchantment(enchantmentName);
                if (xEnchantment.isPresent()) {
                    Enchantment enchantment = xEnchantment.get().parseEnchantment();
                    if (enchantment != null) {
                        if (item.getType() == Material.ENCHANTED_BOOK && meta instanceof EnchantmentStorageMeta) {
                            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
                            esm.addStoredEnchant(enchantment, level, true);
                            item.setItemMeta(esm);
                        } else {
                            meta.addEnchant(enchantment, level, true);
                            item.setItemMeta(meta);
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid enchantment name " + enchantmentName);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid enchantment name " + enchantmentName);
                }
            }
        }
        // Potions
        if (map.containsKey("potion_data")) {
            Map<?, ?> potionDataMap = getMap(map, "potion_data");
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            PotionType potionType = PotionType.valueOf(getString(potionDataMap, "type"));
            boolean extended = false;
            if (potionDataMap.containsKey("extended")) {
                extended = getBoolean(potionDataMap, "extended");
            }
            boolean upgraded = false;
            if (potionDataMap.containsKey("upgraded")) {
                upgraded = getBoolean(potionDataMap, "upgraded");
            }
            PotionData potionData = new PotionData(potionType, extended, upgraded);
            potionMeta.setBasePotionData(potionData);
            item.setItemMeta(potionMeta);
        }
        // Custom potion effects
        if (map.containsKey("custom_effects")) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            for (Map<?, ?> effectMap : getMapList(map, "custom_effects")) {
                String effectName = getString(effectMap, "type");
                PotionEffectType type = PotionEffectType.getByName(effectName);
                if (type != null) {
                    int duration = getInt(effectMap, "duration");
                    int amplifier = getInt(effectMap, "amplifier");
                    potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
                    potionMeta.setColor(type.getColor());
                } else {
                    throw new IllegalArgumentException("Invalid potion effect type " + effectName);
                }
            }
            item.setItemMeta(potionMeta);
        }
        return item;
    }

    protected int[] parseAmount(Map<?, ?> map) {
        if (map.containsKey("amount")) {
            Object object = getElement(map, "amount");
            if (object instanceof Integer) {
                Integer amount = (Integer) object;
                return new int[] {amount, amount};
            } else if (object instanceof String) {
                String amountString = (String) object;
                String[] splitString = amountString.split("-");
                int minAmount = NumberUtils.toInt(splitString[0]);
                int maxAmount = minAmount;
                if (splitString.length > 1) {
                    maxAmount = NumberUtils.toInt(splitString[1], minAmount);
                }
                return new int[] {minAmount, maxAmount};
            }
        }
        return new int[] {1, 1};
    }

}
