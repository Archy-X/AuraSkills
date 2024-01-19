package dev.aurelium.auraskills.bukkit.loot.parser;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.LootManager;
import dev.aurelium.auraskills.bukkit.loot.builder.ItemLootBuilder;
import dev.aurelium.auraskills.bukkit.loot.util.MaterialUtil;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.dbassett.skullcreator.SkullCreator;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ItemLootParser extends LootParser {

    public ItemLootParser(LootManager manager) {
        super(manager);
    }

    @Override
    public Loot parse(Map<?, ?> map) {
        ItemStack item = null;
        // Check if any custom parsers should be used
        for (CustomItemParser parser : manager.getCustomItemParsers()) {
            if (parser.shouldUseParser(map)) {
                item = parser.parseCustomItem(map);
                break;
            }
        }
        // Parse normally
        if (item == null) {
            item = parseItem(map);
        }
        Validate.notNull(item, "Failed to parse item");

        int[] amount = parseAmount(map);

        return new ItemLootBuilder().item(item)
                .minAmount(amount[0])
                .maxAmount(amount[1])
                .message(parseMessage(map))
                .weight(parseWeight(map))
                .contexts(parseContexts(map))
                .options(parseOptions(map))
                .build();
    }

    @SuppressWarnings("deprecation")
    private ItemStack parseItem(Map<?, ?> map) {
        String materialString = getString(map, "material");
        ItemStack item;
        if (!materialString.contains(":")) { // No legacy data
            String materialName = materialString.toUpperCase(Locale.ROOT);
            Material material = MaterialUtil.parse(materialName);
            if (material == null) {
                throw new IllegalArgumentException("Unknown material " + materialString);
            }
            item = new ItemStack(material);
        } else { // With legacy data
            String[] splitMaterial = materialString.split(":");
            if (splitMaterial.length == 2) {
                String materialName = splitMaterial[0].toUpperCase(Locale.ROOT);
                Material material = MaterialUtil.parse(materialName);
                if (material == null) {
                    throw new IllegalArgumentException("Unknown material " + materialName);
                }
                short data = NumberUtils.toShort(splitMaterial[1]);
                item = new ItemStack(material, 1, data);
            } else {
                throw new IllegalArgumentException("Material with data value can only have one :");
            }
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        // Display name and lore
        if (map.containsKey("display_name")) {
            meta = item.getItemMeta();
            String displayName = TextUtil.replaceNonEscaped(getString(map, "display_name"), "&", "§");
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        if (map.containsKey("lore")) {
            meta = item.getItemMeta();
            List<String> lore = getStringList(map, "lore");
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                formattedLore.add(TextUtil.replaceNonEscaped(line, "&", "§"));
            }
            meta.setLore(formattedLore);
            item.setItemMeta(meta);
        }
        // Enchantments
        if (map.containsKey("enchantments")) {
            meta = item.getItemMeta();
            List<String> enchantmentStrings = getStringList(map, "enchantments");
            for (String enchantmentEntry : enchantmentStrings) {
                String[] splitEntry = enchantmentEntry.split(" ");
                String enchantmentName = splitEntry[0];
                int level = 1;
                if (splitEntry.length > 1) {
                    level = NumberUtils.toInt(splitEntry[1], 1);
                }
                if (!ItemUtils.getAndAddEnchant(enchantmentName, level, item, meta)) {
                    throw new IllegalArgumentException("Invalid enchantment name " + enchantmentName);
                }
            }
        }
        // Potions
        if (map.containsKey("potion_data")) {
            Map<?, ?> potionDataMap = getMap(map, "potion_data");
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            PotionType potionType = PotionType.valueOf(getString(potionDataMap, "type").toUpperCase(Locale.ROOT));
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
        // Glowing w/o enchantments visible
        if (getBooleanOrDefault(map, "glow", false)) {
            meta = item.getItemMeta();
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        if (map.containsKey("flags")) {
            meta = item.getItemMeta();
            List<String> flags = getStringList(map, "flags");
            for (String flagName : flags) {
                ItemFlag itemFlag = ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
                meta.addItemFlags(itemFlag);
            }
            item.setItemMeta(meta);
        }
        if (map.containsKey("durability")) {
            meta = item.getItemMeta();
            int durability = getInt(map, "durability");
            if (XMaterial.isNewVersion()) {
                if (meta instanceof Damageable) {
                    Damageable damageable = (Damageable) meta;
                    short maxDurability = item.getType().getMaxDurability();
                    damageable.setDamage(Math.min(maxDurability - durability, maxDurability));
                    item.setItemMeta(meta);
                }
            } else {
                // For old versions
                short maxDurability = item.getType().getMaxDurability();
                item.setDurability((short) Math.max(maxDurability - durability, maxDurability));
            }
        }
        if (map.containsKey("skull_meta")) {
            parseSkullMeta(item, item.getItemMeta(), getMap(map, "skull_meta"));
        }
        // Custom NBT
        if (map.containsKey("nbt")) {
            Object element = getElement(map, "nbt");
            if (element instanceof Map<?, ?>) {
                Map<?, ?> nbtMap = getMap(map, "nbt");
                item = parseNBT(item, nbtMap);
            } else if (element instanceof String) {
                String nbtString = getString(map, "nbt");
                item = parseNBTString(item, nbtString);
            }
        }
        return item;

    }

    private void parseSkullMeta(ItemStack item, ItemMeta meta, Map<?, ?> map) {
        if (!(meta instanceof SkullMeta)) {
            return;
        }
        SkullMeta skullMeta = (SkullMeta) meta;
        if (map.containsKey("uuid")) { // From UUID of player
            UUID id = UUID.fromString(getString(map, "uuid"));
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
            item.setItemMeta(meta);
        }
        if (map.containsKey("base64")) { // From base64 string
            SkullCreator.itemWithBase64(item, getString(map, "base64"));
        }
        if (map.containsKey("url")) { // From Mojang URL
            SkullCreator.itemWithUrl(item, getString(map, "url"));
        }
    }

    private ItemStack parseNBT(ItemStack item, Map<?, ?> map) {
        NBTItem nbtItem = new NBTItem(item);
        applyMapToNBT(nbtItem, map);
        return nbtItem.getItem();
    }

    private void applyMapToNBT(NBTCompound item, Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key instanceof String) {
                if (value instanceof Map<?, ?>) { // Recursively apply sub maps
                    applyMapToNBT(item.getOrCreateCompound((String) key), (Map<?, ?>) value);
                } else {
                    if (value instanceof Integer) {
                        item.setInteger((String) key, (int) value);
                    } else if (value instanceof Double) {
                        item.setDouble((String) key, (double) value);
                    } else if (value instanceof Boolean) {
                        item.setBoolean((String) key, (boolean) value);
                    } else if (value instanceof String) {
                        item.setString((String) key, (String) value);
                    }
                }
            }
        }
    }

    private ItemStack parseNBTString(ItemStack item, String nbtString) {
        NBTContainer container = new NBTContainer(nbtString);
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.mergeCompound(container);
        return nbtItem.getItem();
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
