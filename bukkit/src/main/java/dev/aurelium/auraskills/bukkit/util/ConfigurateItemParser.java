package dev.aurelium.auraskills.bukkit.util;

import com.archyx.slate.util.NumberUtil;
import com.archyx.slate.util.TextUtil;
import com.archyx.slate.util.Validate;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

public class ConfigurateItemParser {

    private final AuraSkills plugin;

    public ConfigurateItemParser(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public ItemStack parseItem(ConfigurationNode config) {
        ItemStack item = parseBaseItem(config);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayName = parseDisplayName(config);
            if (displayName != null) {
                meta.setDisplayName(displayName);
            }
            List<String> lore = parseLore(config);
            if (lore.size() > 0) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack parseBaseItem(ConfigurationNode config) {
        String key = config.node("key").getString();
        if (key != null) {
            ItemStack item = parseItemKey(key);
            if (item != null) {
                return item; // Returns the item if key parse was successful
            }
        }

        String materialString = config.node("material").getString();
        Validate.notNull(materialString, "Item must specify a material");

        ItemStack item = parseMaterialString(materialString);

        parseAmount(item, config);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        // Enchantments
        if (!config.node("enchantments").virtual()) {
            parseEnchantments(item, config);
        }
        // Potions
        ConfigurationNode potionDataSection = config.node("potion_data");
        if (!potionDataSection.virtual()) {
            parsePotionData(item, potionDataSection);
        }
        // Custom potion effects
        if (!config.node("custom_effects").virtual()) {
            parseCustomEffects(config, item);
        }
        // Glowing w/o enchantments visible
        if (config.node("glow").getBoolean(false)) {
            parseGlow(item);
        }
        // Custom NBT
        if (!config.node("nbt").virtual()) {
            if (config.node("nbt").isMap()) {
                ConfigurationNode nbtSection = config.node("nbt");
                item = parseNBT(item, nbtSection.childrenMap());
            } else if (config.node("nbt").getString() != null) {
                String nbtString = config.getString("nbt");
                if (nbtString != null) {
                    item = parseNBTString(item, nbtString);
                }
            }
        }
        if (!config.node("flags").virtual()) {
            parseFlags(config, item);
        }
        if (!config.node("durability").virtual()) {
            parseDurability(config, item);
        }
        ConfigurationNode skullMetaSection = config.node("skull_meta");
        if (!skullMetaSection.virtual()) {
            parseSkullMeta(item, item.getItemMeta(), skullMetaSection);
        }
        return item;
    }

    @Nullable
    private ItemStack parseItemKey(String key) {
        return plugin.getItemRegistry().getItem(NamespacedId.fromStringOrDefault(key));
    }

    @SuppressWarnings("deprecation")
    private void parseDurability(ConfigurationNode section, ItemStack item) {
        ItemMeta meta = getMeta(item);
        int durability = section.node("durability").getInt();
        if (XMaterial.isNewVersion()) {
            if (meta instanceof Damageable damageable) {
                short maxDurability = item.getType().getMaxDurability();
                damageable.setDamage(Math.max(maxDurability - durability, maxDurability));
                item.setItemMeta(meta);
            }
        } else {
            // For old versions
            short maxDurability = item.getType().getMaxDurability();
            item.setDurability((short) Math.max(maxDurability - durability, maxDurability));
        }
    }

    private void parseFlags(ConfigurationNode section, ItemStack item) {
        try {
            ItemMeta meta = getMeta(item);
            List<String> flags = section.node("flags").getList(String.class, new ArrayList<>());
            for (String flagName : flags) {
                ItemFlag itemFlag = ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
                meta.addItemFlags(itemFlag);
            }
            item.setItemMeta(meta);
        } catch (SerializationException ignored) {

        }
    }

    private void parseGlow(ItemStack item) {
        ItemMeta meta = getMeta(item);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    private void parseCustomEffects(ConfigurationNode section, ItemStack item) {
        PotionMeta potionMeta = (PotionMeta) getMeta(item);
        for (ConfigurationNode effectNode : section.node("custom_effects").childrenList()) {
            String effectName = effectNode.node("type").getString("SPEED");
            PotionEffectType type = PotionEffectType.getByName(effectName);
            if (type != null) {
                int duration = effectNode.node("duration").getInt();
                int amplifier = effectNode.node("amplifier").getInt();
                potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
                potionMeta.setColor(type.getColor());
            } else {
                throw new IllegalArgumentException("Invalid potion effect type " + effectName);
            }
        }
        item.setItemMeta(potionMeta);
    }

    private void parsePotionData(ItemStack item, ConfigurationNode node) {
        PotionMeta potionMeta = (PotionMeta) getMeta(item);
        PotionType potionType = PotionType.valueOf(node.node("type").getString("WATER").toUpperCase(Locale.ROOT));
        boolean extended = node.node("extended").getBoolean(false);
        boolean upgraded = node.node("upgraded").getBoolean(false);

        PotionData potionData = new PotionData(potionType, extended, upgraded);
        potionMeta.setBasePotionData(potionData);
        item.setItemMeta(potionMeta);
    }

    private void parseEnchantments(ItemStack item, ConfigurationNode section) {
        try {
            ItemMeta meta = getMeta(item);
            List<String> enchantmentStrings = section.node("enchantments").getList(String.class, new ArrayList<>());
            for (String enchantmentEntry : enchantmentStrings) {
                String[] splitEntry = enchantmentEntry.split(" ");
                String enchantmentName = splitEntry[0];
                int level = 1;
                if (splitEntry.length > 1) {
                    level = NumberUtil.toInt(splitEntry[1], 1);
                }
                Optional<XEnchantment> xEnchantment = XEnchantment.matchXEnchantment(enchantmentName.toUpperCase(Locale.ROOT));
                if (xEnchantment.isPresent()) {
                    Enchantment enchantment = xEnchantment.get().parseEnchantment();
                    if (enchantment != null) {
                        if (item.getType() == Material.ENCHANTED_BOOK && meta instanceof EnchantmentStorageMeta esm) {
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
        } catch (SerializationException ignored) {

        }
    }

    @SuppressWarnings("deprecation")
    private ItemStack parseMaterialString(String materialString) {
        ItemStack item;
        String materialName = materialString.toUpperCase(Locale.ROOT);
        Material material = parseMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material " + materialString);
        }
        if (!material.isItem()) { // Return fallback item if material isn't an item
            return new ItemStack(Material.GRAY_DYE);
        }
        item = new ItemStack(material);
        return item;
    }

    private @NotNull ItemMeta getMeta(ItemStack item) {
        return Objects.requireNonNull(item.getItemMeta());
    }

    @Nullable
    public String parseDisplayName(ConfigurationNode section) {
        if (!section.node("display_name").virtual()) {
            return TextUtil.applyColor(section.node("display_name").getString());
        }
        return null;
    }

    @NotNull
    public List<String> parseLore(ConfigurationNode section) {
        try {
            List<String> lore = section.node("lore").getList(String.class, new ArrayList<>());
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                line = TextUtil.applyColor(line);
                formattedLore.add(line);
            }
            return formattedLore;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    private ItemStack parseNBT(ItemStack item, Map<Object, ? extends ConfigurationNode> map) {
        NBTItem nbtItem = new NBTItem(item);
        applyMapToNBT(nbtItem, map);
        return nbtItem.getItem();
    }

    private void applyMapToNBT(NBTCompound item, Map<Object, ? extends ConfigurationNode> map) {
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue().raw();
            if (key instanceof String) {
                if (value instanceof ConfigurationNode childNode) { // Recursively apply sub maps
                    applyMapToNBT(item.getOrCreateCompound((String) key), childNode.childrenMap());
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

    protected Material parseMaterial(String name) {
        Material material = Material.getMaterial(name);
        if (material != null) {
            return material;
        }
        Optional<XMaterial> materialOptional = XMaterial.matchXMaterial(name);
        return materialOptional.map(XMaterial::parseMaterial).orElse(null);
    }

    private void parseSkullMeta(ItemStack item, ItemMeta meta, ConfigurationNode section) {
        if (!(meta instanceof SkullMeta skullMeta)) {
            return;
        }
        String uuid = section.node("uuid").getString();
        if (uuid != null) { // From UUID of player
            UUID id = UUID.fromString(uuid);
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
            item.setItemMeta(meta);
        }
        String base64 = section.node("base64").getString();
        if (base64 != null) { // From base64 string
            SkullCreator.itemWithBase64(item, base64);
        }
        String url = section.node("url").getString();
        if (url != null) { // From Mojang URL
            SkullCreator.itemWithUrl(item, url);
        }
        if (XMaterial.getVersion() >= 14) { // Persistent data container requires 1.14+
            String placeholder = section.node("placeholder_uuid").getString();
            if (placeholder != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "skull_placeholder_uuid");
                container.set(key, PersistentDataType.STRING, placeholder);
                item.setItemMeta(meta);
            }
        }
    }

    private void parseAmount(ItemStack item, ConfigurationNode section) {
        int amount = section.node("amount").getInt(1);
        item.setAmount(amount);
    }

}
