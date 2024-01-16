package dev.aurelium.auraskills.bukkit.loot;

import com.cryptomorin.xseries.XMaterial;
import dev.aurelium.auraskills.bukkit.loot.parser.CommandLootParser;
import dev.aurelium.auraskills.bukkit.loot.parser.ItemLootParser;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import dev.aurelium.auraskills.common.util.data.Parser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class LootLoader extends Parser {

    private final LootManager manager;

    public LootLoader(LootManager manager) {
        this.manager = manager;
    }

    public LootTable loadLootTable(File file, FileConfiguration config) {
        // Parse loot table type, default to block
        String typeString = config.getString("type");
        LootTableType type = LootTableType.BLOCK;
        if (typeString != null) {
            type = LootTableType.valueOf(typeString.toUpperCase(Locale.ROOT));
        }
        // Load pools
        ConfigurationSection poolsSection = config.getConfigurationSection("pools");
        if (poolsSection == null) return null;
        List<LootPool> pools = new ArrayList<>();
        for (String poolName : poolsSection.getKeys(false)) {
            ConfigurationSection currentPool = poolsSection.getConfigurationSection(poolName);
            if (currentPool == null) continue;

            double baseChance = currentPool.getDouble("base_chance", 1.0) / 100; // Converts from percent chance to decimal
            int selectionPriority = currentPool.getInt("selection_priority", 1);
            boolean overrideVanillaLoot = currentPool.getBoolean("override_vanilla_loot", false);

            // Load pool options
            Map<String, Object> options = new HashMap<>();
            for (String optionKey : manager.getPoolOptionKeys()) {
                if (currentPool.contains(optionKey)) {
                    Object option = currentPool.get(optionKey);
                    options.put(optionKey, option);
                }
            }

            // Parse each loot entry
            List<Map<?,?>> lootMapList = currentPool.getMapList("loot");
            List<Loot> lootList = new ArrayList<>();
            int index = 0;
            for (Map<?, ?> lootEntryMap : lootMapList) {
                Loot loot = null;
                try {
                    String lootType = DataUtil.getString(lootEntryMap, "type");
                    // Item loot
                    if (lootType.equalsIgnoreCase("item")) {
                        if (getBooleanOrDefault(lootEntryMap, "ignore_legacy", false) && XMaterial.getVersion() <= 12) {
                            index++;
                            continue;
                        }
                        loot = new ItemLootParser(manager).parse(lootEntryMap);
                    }
                    // Command loot
                    else if (lootType.equalsIgnoreCase("command")) {
                        loot = new CommandLootParser(manager).parse(lootEntryMap);
                    } else {
                        throw new IllegalArgumentException("Unknown loot type: " + lootType);
                    }
                } catch (Exception e) {
                    manager.getPlugin().getLogger().warning("Error parsing loot in file loot/" + file.getName() + " at path pools." + poolName + ".loot." + index + ", see below for error:");
                    e.printStackTrace();
                }
                if (loot != null) {
                    lootList.add(loot);
                }
                index++;
            }
            // Create pool
            LootPool pool = new LootPool(poolName, lootList, baseChance, selectionPriority, overrideVanillaLoot, options);
            pools.add(pool);
        }
        // Sort pools by selection priority
        pools.sort((pool1, pool2) -> pool2.getSelectionPriority() - pool1.getSelectionPriority());
        // Create table
        String fileName = file.getName();
        String tableName = fileName.substring(0, fileName.lastIndexOf("."));
        return new LootTable(tableName, UUID.randomUUID(), type, pools);
    }

}
