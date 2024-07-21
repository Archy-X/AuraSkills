package dev.aurelium.auraskills.bukkit.loot;

import dev.aurelium.auraskills.api.loot.*;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.loot.parser.CommandLootParser;
import dev.aurelium.auraskills.bukkit.loot.parser.EntityLootParser;
import dev.aurelium.auraskills.bukkit.loot.parser.ItemLootParser;
import dev.aurelium.auraskills.bukkit.loot.parser.LootParsingContextImpl;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import dev.aurelium.auraskills.common.util.data.Parser;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.*;

public class LootLoader extends Parser {

    private final LootManager manager;

    public LootLoader(LootManager manager) {
        this.manager = manager;
    }

    public LootTable loadLootTable(NamespacedId id, File file, ConfigurationNode config) {
        // Parse loot table type, default to block
        String typeString = config.node("type").getString();
        LootTableType type = LootTableType.BLOCK;
        if (typeString != null) {
            type = LootTableType.valueOf(typeString.toUpperCase(Locale.ROOT));
        }
        // Load pools
        ConfigurationNode poolsNode = config.node("pools");
        if (poolsNode.virtual()) return null;

        List<LootPool> pools = new ArrayList<>();
        for (ConfigurationNode poolNode : poolsNode.childrenMap().values()) {
            String poolName = (String) poolNode.key();

            double baseChance = poolNode.node("base_chance").getDouble(0) / 100; // Converts from percent chance to decimal
            int selectionPriority = poolNode.node("selection_priority").getInt(1);
            boolean overrideVanillaLoot = poolNode.node("override_vanilla_loot").getBoolean(false);

            // Load pool options
            Map<String, Object> options = new HashMap<>();
            for (String optionKey : manager.getPoolOptionKeys()) {
                if (poolNode.hasChild(optionKey)) {
                    Object option = poolNode.node(optionKey).raw();
                    options.put(optionKey, option);
                }
            }

            // Parse each loot entry
            List<Loot> lootList = new ArrayList<>();
            int index = 0;
            for (ConfigurationNode lootNode : poolNode.node("loot").childrenList()) {
                Loot loot = null;
                try {
                    String lootType = lootNode.node("type").getString("");
                    // Item loot
                    LootParsingContext context = new LootParsingContextImpl(manager);
                    if (lootType.equalsIgnoreCase("item")) {
                        // Ignore loot if below the ignore_below major version
                        int ignoreBelow = lootNode.node("ignore_below").getInt(-1);
                        if (ignoreBelow != -1 && !VersionUtils.isAtLeastVersion(ignoreBelow)) {
                            index++;
                            continue;
                        }

                        loot = new ItemLootParser(manager).parse(context, ApiConfigNode.toApi(lootNode));
                    }
                    // Command loot
                    else if (lootType.equalsIgnoreCase("command")) {
                        loot = new CommandLootParser().parse(context, ApiConfigNode.toApi(lootNode));
                    // Entity loot, mainly for fishing
                    } else if (lootType.equalsIgnoreCase("entity")) {
                        loot = new EntityLootParser(manager).parse(context, ApiConfigNode.toApi(lootNode));
                    } else {
                        // Parse custom loot registered from API
                        LootParser customParser = manager.getCustomLootParsers().get(lootType);
                        if (customParser == null) {
                            throw new IllegalArgumentException("Unknown loot type: " + lootType);
                        }

                        loot = customParser.parse(context, ApiConfigNode.toApi(lootNode));
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
        return new LootTable(id, UUID.randomUUID(), type, pools);
    }

}
