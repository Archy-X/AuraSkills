package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.LootManager;
import dev.aurelium.auraskills.bukkit.loot.builder.ItemLootBuilder;
import dev.aurelium.auraskills.bukkit.loot.item.ItemSupplier;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantEntry;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantList;
import dev.aurelium.auraskills.bukkit.loot.item.enchant.LootEnchantments;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.util.data.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

public class ItemLootParser extends LootParser {

    private final ConfigurateItemParser itemParser;

    public ItemLootParser(LootManager manager) {
        super(manager);
        this.itemParser = new ConfigurateItemParser(manager.getPlugin());
    }

    @Override
    public Loot parse(ConfigurationNode config) throws SerializationException {
        ItemSupplier item = null;
        // Check if any custom parsers should be used
        for (CustomItemParser parser : manager.getCustomItemParsers()) {
            if (parser.shouldUseParser(config)) {
                item = new ItemSupplier(parser.parseCustomItem(config), null);
                break;
            }
        }
        // Parse normally
        if (item == null) {
            item = parseItem(config);
        }
        Validate.notNull(item, "Failed to parse item");

        int[] amount = parseAmount(config);

        return new ItemLootBuilder().item(item)
                .minAmount(amount[0])
                .maxAmount(amount[1])
                .message(parseMessage(config))
                .weight(parseWeight(config))
                .contexts(parseContexts(config))
                .options(parseOptions(config))
                .build();
    }

    private ItemSupplier parseItem(ConfigurationNode config) throws SerializationException {
        List<String> excludedKeys = List.of("amount", "enchantments");
        ItemStack baseItem = itemParser.parseItem(config, excludedKeys);
        // Parse possible enchantments
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
                    minLevel = NumberUtils.toInt(splitLevel[0], 1);
                    maxLevel = NumberUtils.toInt(splitLevel[1], 1);
                } else {
                    int fixedLevel = NumberUtils.toInt(splitEntry[1], 1);
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
