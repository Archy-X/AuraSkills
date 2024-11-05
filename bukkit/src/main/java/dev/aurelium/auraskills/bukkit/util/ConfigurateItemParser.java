package dev.aurelium.auraskills.bukkit.util;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import dev.aurelium.auraskills.api.item.ItemContext;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.BukkitPotionType;
import dev.aurelium.auraskills.common.util.PlatformUtil;
import dev.aurelium.auraskills.common.util.data.Validate;
import dev.aurelium.slate.context.ContextGroup;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.position.FixedPosition;
import dev.aurelium.slate.position.GroupPosition;
import dev.aurelium.slate.position.PositionProvider;
import dev.aurelium.slate.util.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

public class ConfigurateItemParser {

    private final AuraSkills plugin;

    public ConfigurateItemParser(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public ItemStack parseItem(ConfigurationNode config) {
        return parseItem(config, new ArrayList<>());
    }

    public ItemStack parseItem(ConfigurationNode config, List<String> excludedKeys) {
        ItemStack item = parseBaseItem(config, excludedKeys);
        parseDisplayNameAndLore(item, config);
        return item;
    }

    public List<ItemStack> parseMultipleItems(ConfigurationNode config) throws SerializationException {
        if (config.node("materials").virtual()) {
            return List.of(parseBaseItem(config));
        }
        List<String> materialList = config.node("materials").getList(String.class);
        if (materialList == null) return List.of(parseBaseItem(config));

        List<ItemStack> parsedItems = new ArrayList<>();

        for (String materialString : materialList) {
            ItemStack item = parseMaterialString(materialString);
            item = parseItemMeta(item, config, new ArrayList<>());
            parseDisplayNameAndLore(item, config);

            parsedItems.add(item);
        }

        return parsedItems;
    }

    public ItemStack parseBaseItem(ConfigurationNode config) {
        return parseBaseItem(config, new ArrayList<>());
    }

    public ItemStack parseBaseItem(ConfigurationNode config, List<String> excludedKeys) {
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
        item = parseItemMeta(item, config, excludedKeys);

        return item;
    }

    public ItemStack parseItemMeta(ItemStack item, ConfigurationNode config, List<String> excludedKeys) {
        if (!excludedKeys.contains("amount")) {
            parseAmount(item, config);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        // Enchantments
        if (config.hasChild("enchantments") && !excludedKeys.contains("enchantments")) {
            parseEnchantments(item, config);
        }
        // Potions
        if (config.hasChild("potion_data") && !excludedKeys.contains("potion_data")) {
            parsePotionData(item, config.node("potion_data"));
        }
        // Custom potion effects
        if (config.hasChild("custom_effects") && !excludedKeys.contains("custom_effects")) {
            parseCustomEffects(config, item);
        }
        // Glowing w/o enchantments visible
        if (config.node("glow").getBoolean(false)) {
            parseGlow(item);
        }
        // Custom NBT
        if (!config.node("nbt").virtual() && !excludedKeys.contains("nbt")) {
            item = parseNBT(item, config.node("nbt"));
        }
        if (!config.node("flags").virtual() && !excludedKeys.contains("flags")) {
            parseFlags(config, item);
        }
        if (!config.node("durability").virtual() && !excludedKeys.contains("durability")) {
            parseDurability(config, item);
        }
        // Parses custom_model_data and old format CustomModelData nbt map
        parseCustomModelData(config, item);
        parseHideTooltip(config, item);

        ConfigurationNode skullMetaSection = config.node("skull_meta");
        if (!skullMetaSection.virtual() && !excludedKeys.contains("skull_meta")) {
            parseSkullMeta(item, item.getItemMeta(), skullMetaSection);
        }
        return item;
    }

    public ItemStack parseNBT(ItemStack item, ConfigurationNode config) {
        if (plugin.isNbtApiDisabled()) {
            return item;
        }
        if (config.isMap()) {
            parseNBT(item, config.childrenMap());
        } else if (config.getString() != null) {
            String nbtString = config.getString("nbt");
            if (nbtString != null) {
                parseNBTString(item, nbtString);
            }
        }
        return item;
    }

    public void parseDisplayNameAndLore(ItemStack item, ConfigurationNode config) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String displayName = parseDisplayName(config);
        if (displayName != null) {
            meta.setDisplayName(displayName);
        }
        List<String> lore = parseLore(config);
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    @Nullable
    private ItemStack parseItemKey(String key) {
        return plugin.getItemRegistry().getItem(NamespacedId.fromDefault(key));
    }

    private void parseDurability(ConfigurationNode section, ItemStack item) {
        ItemMeta meta = getMeta(item);
        int durability = section.node("durability").getInt();
        if (meta instanceof Damageable damageable) {
            short maxDurability = item.getType().getMaxDurability();
            damageable.setDamage(Math.max(maxDurability - durability, maxDurability));
            item.setItemMeta(meta);
        }
    }

    private void parseFlags(ConfigurationNode section, ItemStack item) {
        try {
            ItemMeta meta = getMeta(item);
            List<String> flags = section.node("flags").getList(String.class, new ArrayList<>());
            for (String flagName : flags) {
                ItemFlag itemFlag = ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
                if (itemFlag == ItemFlag.HIDE_ATTRIBUTES && VersionUtils.isAtLeastVersion(20, 5)) {
                    meta.setAttributeModifiers(Material.IRON_SWORD.getDefaultAttributeModifiers(EquipmentSlot.HAND));
                }
                meta.addItemFlags(itemFlag);
            }
            item.setItemMeta(meta);
        } catch (SerializationException ignored) {

        }
    }

    private void parseGlow(ItemStack item) {
        ItemMeta meta = getMeta(item);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    @SuppressWarnings("deprecation")
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

    public void parsePotionData(ItemStack item, ConfigurationNode node) {
        PotionMeta potionMeta = (PotionMeta) getMeta(item);
        PotionType potionType = PotionType.valueOf(node.node("type").getString("WATER").toUpperCase(Locale.ROOT));
        boolean extended = node.node("extended").getBoolean(false);
        boolean upgraded = node.node("upgraded").getBoolean(false);

        BukkitPotionType bukkitPotionType = new BukkitPotionType(potionType, extended, upgraded);
        bukkitPotionType.applyToMeta(potionMeta);

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
                if (!ItemUtils.getAndAddEnchant(enchantmentName, level, item, meta)) {
                    throw new IllegalArgumentException("Invalid enchantment name " + enchantmentName);
                }
            }
        } catch (SerializationException ignored) {

        }
    }

    public SlotPos parsePosition(String input) {
        String[] splitInput = input.split(",", 2);
        if (splitInput.length == 2) {
            int row = Integer.parseInt(splitInput[0]);
            int column = Integer.parseInt(splitInput[1]);
            return SlotPos.of(row, column);
        } else {
            int slot = Integer.parseInt(input);
            int row = slot / 9;
            int column = slot % 9;
            return SlotPos.of(row, column);
        }
    }

    public ConfigurationNode parseItemContext(ItemContext itemContext) throws SerializationException {
        ConfigurationNode config = CommentedConfigurationNode.root();
        for (Map.Entry<String, Object> entry : itemContext.getMap().entrySet()) {
            config.node(entry.getKey()).set(entry.getValue());
        }
        return config;
    }

    @Nullable
    public PositionProvider parsePositionProvider(ConfigurationNode config, ActiveMenu activeMenu, String templateName) {
        String pos = config.node("pos").getString();
        if (pos != null) {
            // Static position
            SlotPos slotPos = parsePosition(pos);
            return new FixedPosition(slotPos);
        } else if (!config.node("group").virtual()) {
            // Group and order position
            String groupName = config.node("group").getString("");
            ContextGroup contextGroup = activeMenu.getContextGroups(templateName).get(groupName);
            if (contextGroup != null) {
                int order = config.node("order").getInt();

                return new GroupPosition(contextGroup, order);
            }
        }
        return null;
    }

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

    public @NotNull ItemMeta getMeta(ItemStack item) {
        return Objects.requireNonNull(item.getItemMeta());
    }

    @Nullable
    public String parseDisplayName(ConfigurationNode section) {
        if (!section.node("display_name").virtual()) {
            PlatformUtil util = plugin.getPlatformUtil();
            return util.toString(util.toComponent(section.node("display_name").getString()));
        }
        return null;
    }

    @NotNull
    public List<String> parseLore(ConfigurationNode section) {
        try {
            List<String> lore = section.node("lore").getList(String.class, new ArrayList<>());
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                PlatformUtil util = plugin.getPlatformUtil();
                line = util.toString(util.toComponent(line));
                formattedLore.add(line);
            }
            return formattedLore;
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

    private void parseNBT(ItemStack item, Map<Object, ? extends ConfigurationNode> map) {
        NBT.modify(item, nbt -> {
            applyMapToNBT(nbt, map);
        });
    }

    private void applyMapToNBT(ReadWriteNBT item, Map<Object, ? extends ConfigurationNode> map) {
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : map.entrySet()) {
            Object keyObj = entry.getKey();
            Object value = entry.getValue().raw();
            if (keyObj instanceof String key) {
                if (key.equals("CustomModelData")) { // Parsed elsewhere
                    continue;
                }
                if (value instanceof ConfigurationNode childNode) { // Recursively apply sub maps
                    applyMapToNBT(item.getOrCreateCompound(key), childNode.childrenMap());
                } else {
                    if (value instanceof Integer) {
                        item.setInteger(key, (int) value);
                    } else if (value instanceof Double) {
                        item.setDouble(key, (double) value);
                    } else if (value instanceof Boolean) {
                        item.setBoolean(key, (boolean) value);
                    } else if (value instanceof String) {
                        item.setString(key, (String) value);
                    }
                }
            }
        }
    }

    private void parseNBTString(ItemStack item, String nbtString) {
        ReadWriteNBT container = NBT.parseNBT(nbtString);
        NBT.modify(item, nbt -> {
            nbt.mergeCompound(container);
        });
    }

    @Nullable
    protected Material parseMaterial(String name) {
        if (name.equals("SCUTE") && VersionUtils.isAtLeastVersion(20, 5)) {
            return Material.TURTLE_SCUTE;
        } else if (name.equals("TURTLE_SCUTE") && !VersionUtils.isAtLeastVersion(20, 5)) {
            return Material.valueOf("SCUTE");
        }
        return Material.getMaterial(name);
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
        if (VersionUtils.isAtLeastVersion(14)) { // Persistent data container requires 1.14+
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

    private void parseCustomModelData(ConfigurationNode config, ItemStack item) {
        if (!config.node("custom_model_data").virtual()) {
            int data = config.node("custom_model_data").getInt();
            ItemMeta meta = getMeta(item);
            meta.setCustomModelData(data);
            item.setItemMeta(meta);
        } else if (!config.node("nbt").node("CustomModelData").virtual()) {
            int data = config.node("nbt").node("CustomModelData").getInt();
            ItemMeta meta = getMeta(item);
            meta.setCustomModelData(data);
            item.setItemMeta(meta);
        }
    }

    public void parseHideTooltip(ConfigurationNode config, ItemStack item) {
        if (!config.node("hide_tooltip").virtual()) {
            boolean hideTooltip = config.node("hide_tooltip").getBoolean();
            ItemMeta meta = getMeta(item);
            if (VersionUtils.isAtLeastVersion(20, 5)) {
                meta.setHideTooltip(hideTooltip);
            }
            item.setItemMeta(meta);
        }
    }

}
