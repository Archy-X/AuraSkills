package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootParser;
import dev.aurelium.auraskills.api.loot.LootParsingContext;
import dev.aurelium.auraskills.bukkit.loot.LootManager;
import dev.aurelium.auraskills.bukkit.loot.item.ItemSupplier;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantEntry;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantList;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantments;
import dev.aurelium.auraskills.bukkit.loot.type.ItemLoot;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import dev.aurelium.auraskills.common.util.data.Validate;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

public class ItemLootParser implements LootParser {

    private final LootManager manager;
    private final ConfigurateItemParser itemParser;

    public ItemLootParser(LootManager manager) {
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

        return new ItemLoot(context.parseValues(config), item, amounts[0], amounts[1]);
    }

    private ItemSupplier parseItem(ConfigurationNode config) throws SerializationException {
        List<String> excludedKeys = List.of("amount", "enchantments");
        ItemStack baseItem = itemParser.parseItem(config, excludedKeys);
        // Parse possible enchantments, value of the map is the weight
        Map<LootEnchantList, Integer> possibleEnchants = parsePossibleEnchants(config);

        return new ItemSupplier(baseItem, new LootEnchantments(possibleEnchants));
    }

    private Map<LootEnchantList, Integer> parsePossibleEnchants(ConfigurationNode config) throws SerializationException {
        Map<LootEnchantList, Integer> possibleEnchants = new HashMap<>();
        if (config.hasChild("enchantments")) { // Single enchant list
            LootEnchantList singleList = parseSingleEnchantList(config);
            possibleEnchants.put(singleList, 1);
        }
        return possibleEnchants;
    }

    private LootEnchantList parseSingleEnchantList(ConfigurationNode config) throws SerializationException {
        List<LootEnchantEntry> entryList = new ArrayList<>();

        List<String> enchantmentStrings = config.node("enchantments").getList(String.class, new ArrayList<>());
        for (String enchantmentEntry : enchantmentStrings) {
            String[] splitEntry = enchantmentEntry.split(" ");
            String enchantmentName = splitEntry[0].toLowerCase(Locale.ROOT);
            int minLevel = 1;
            int maxLevel = 1;
            if (splitEntry.length > 1) {
                String levelString = splitEntry[1];
                if (levelString.contains("-")) { // Handle level range format (eg. 1-5)
                    String[] splitLevel = levelString.split("-");
                    minLevel = Integer.parseInt(splitLevel[0]);
                    maxLevel = Integer.parseInt(splitLevel[1]);
                } else {
                    int fixedLevel = Integer.parseInt(splitEntry[1]);
                    minLevel = fixedLevel;
                    maxLevel = fixedLevel;
                }
            }
            LootEnchantEntry entry = new LootEnchantEntry(enchantmentName, minLevel, maxLevel);
            entryList.add(entry);
        }
        return new LootEnchantList(entryList);
    }

    protected int[] parseAmount(ConfigurationNode config) {
        if (config.hasChild("amount")) {
            Object object = config.node("amount").raw();
            if (object instanceof Integer amount) {
                return new int[] {amount, amount};
            } else if (object instanceof String amountString) {
                String[] splitString = amountString.split("-");
                int minAmount = Integer.parseInt(splitString[0]);
                int maxAmount = minAmount;
                if (splitString.length > 1) {
                    maxAmount = Integer.parseInt(splitString[1]);
                }
                return new int[] {minAmount, maxAmount};
            }
        }
        return new int[] {1, 1};
    }

}
