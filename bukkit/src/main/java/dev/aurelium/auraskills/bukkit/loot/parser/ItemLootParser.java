package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootParser;
import dev.aurelium.auraskills.api.loot.LootParsingContext;
import dev.aurelium.auraskills.bukkit.loot.BukkitLootManager;
import dev.aurelium.auraskills.bukkit.loot.item.ItemSupplier;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantEntry;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantLevel;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantList;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantments;
import dev.aurelium.auraskills.bukkit.loot.type.ItemLoot;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import dev.aurelium.auraskills.common.loot.CustomItemParser;
import dev.aurelium.auraskills.common.util.data.Validate;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

import static dev.aurelium.auraskills.bukkit.ref.BukkitItemRef.wrap;

public class ItemLootParser implements LootParser {

    private final BukkitLootManager manager;
    private final ConfigurateItemParser itemParser;

    public ItemLootParser(BukkitLootManager manager) {
        this.manager = manager;
        this.itemParser = new ConfigurateItemParser(manager.getPlugin());
    }

    @Override
    public Loot parse(LootParsingContext context, ConfigNode config) {
        ItemSupplier item = null;
        ConfigurationNode backing = ((ApiConfigNode) config).getBacking();
        for (CustomItemParser parser : manager.getCustomItemParsers()) {
            if (parser.shouldUseParser(backing)) {
                item = new ItemSupplier(parser.parseCustomItem(backing), null);
                break;
            }
        }
        // Parse normally
        if (item == null) {
            try {
                item = parseItem(backing);
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }
        }
        Validate.notNull(item, "Failed to parse item");

        int[] amounts = parseAmount(backing);
        double[] durability = parseDurability(backing);

        return new ItemLoot(context.parseValues(config), item, amounts[0], amounts[1], durability[0], durability[1]);
    }

    private ItemSupplier parseItem(ConfigurationNode config) throws SerializationException {
        List<String> excludedKeys = List.of("amount", "enchantments");
        ItemStack baseItem = itemParser.parseItem(config, excludedKeys);
        // Parse possible enchantments, value of the map is the weight
        Map<LootEnchantList, Integer> possibleEnchants = parsePossibleEnchants(config);

        return new ItemSupplier(wrap(baseItem), new LootEnchantments(possibleEnchants));
    }

    private Map<LootEnchantList, Integer> parsePossibleEnchants(ConfigurationNode config) throws SerializationException {
        Map<LootEnchantList, Integer> possibleEnchants = new HashMap<>();
        if (config.hasChild("enchantments")) { // Single enchant list
            LootEnchantList singleList = parseSingleEnchantList(config);
            possibleEnchants.put(singleList, 1);
        }
        return possibleEnchants;
    }

    private LootEnchantList parseSingleEnchantList(ConfigurationNode config) {
        List<LootEnchantEntry> entryList = new ArrayList<>();

        for (ConfigurationNode enchantment : config.node("enchantments").childrenList()) {
            String enchantmentName;
            String levelString = "1";
            double chance = 1.0;

            if (enchantment.isMap()) {
                enchantmentName = enchantment.node("name").getString("").toLowerCase(Locale.ROOT);
                levelString = enchantment.node("level").getString("1");
                chance = Double.parseDouble(enchantment.node("chance").getString("1.0"));
            } else {
                String[] splitEntry = enchantment.getString("").split(" ");
                enchantmentName = splitEntry[0].toLowerCase(Locale.ROOT);
                if (splitEntry.length > 1) {
                    levelString = splitEntry[1];
                }
            }

            LootEnchantLevel enchantLevel = parseLevel(levelString);
            LootEnchantEntry entry = new LootEnchantEntry(enchantmentName, enchantLevel.minLevel(), enchantLevel.maxLevel(), chance);
            entryList.add(entry);
        }

        return new LootEnchantList(entryList);
    }

    protected LootEnchantLevel parseLevel(String levelString) {
        int minLevel;
        int maxLevel;

        if (levelString.contains("-")) { // Handle level range format (eg. 1-5)
            String[] splitLevel = levelString.split("-");
            minLevel = Integer.parseInt(splitLevel[0]);
            maxLevel = Integer.parseInt(splitLevel[1]);
        } else {
            int fixedLevel = Integer.parseInt(levelString);
            minLevel = fixedLevel;
            maxLevel = fixedLevel;
        }

        return new LootEnchantLevel(minLevel, maxLevel);
    }

    protected int[] parseAmount(ConfigurationNode config) {
        if (config.hasChild("amount")) {
            Object object = config.node("amount").raw();
            if (object instanceof Integer amount) {
                return new int[]{amount, amount};
            } else if (object instanceof String amountString) {
                String[] splitString = amountString.split("-");
                int minAmount = Integer.parseInt(splitString[0]);
                int maxAmount = minAmount;
                if (splitString.length > 1) {
                    maxAmount = Integer.parseInt(splitString[1]);
                }
                return new int[]{minAmount, maxAmount};
            }
        }
        return new int[]{1, 1};
    }

    protected double[] parseDurability(ConfigurationNode config) {
        double minDamage = 0.0;
        double maxDamage = 0.0;

        if (config.hasChild("damage")) {
            if (config.node("damage").isMap()) {
                minDamage = config.node("damage", "min").getDouble();
                maxDamage = config.node("damage", "max").getDouble();
            } else {
                minDamage = config.node("damage").getDouble();
                maxDamage = minDamage;
            }
        }

        return new double[]{minDamage, maxDamage};
    }

}
