package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.api.loot.*;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.api.ApiAuraSkills;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.config.Option;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class LootLoader {

    private final AuraSkillsPlugin plugin;
    private final LootManager manager;

    public LootLoader(AuraSkillsPlugin plugin, LootManager lootManager) {
        this.plugin = plugin;
        this.manager = lootManager;
    }

    public abstract LootParser getParser(LootType type);

    public void loadLootTables() {
        // Check that new loot files do not exist yet for conversion
        boolean convertFishing = !new File(plugin.getPluginFolder() + "/loot", "fishing.yml").exists();
        boolean convertExcavation = !new File(plugin.getPluginFolder() + "/loot", "excavation.yml").exists();
        // Generate default loot files
        File lootDirectory = new File(plugin.getPluginFolder() + "/loot");
        if (!lootDirectory.exists() || convertFishing || convertExcavation) {
            generateDefaultLootTables();
        }
        if (!lootDirectory.isDirectory()) return;

        manager.getLootTables().clear();
        File[] files = lootDirectory.listFiles();
        if (files == null) return;
        for (File lootTableFile : files) {
            if (!lootTableFile.isFile() || !lootTableFile.getName().endsWith(".yml")) {
                continue;
            }
            ConfigurateLoader loader = new ConfigurateLoader(plugin, TypeSerializerCollection.builder().build());
            try {
                // Load user file
                ConfigurationNode user = loader.loadUserFile(lootTableFile);
                // Merge embedded and user nodes to ensure config has all options
                ConfigurationNode config = user;

                String path = plugin.getPluginFolder().toPath().relativize(lootTableFile.toPath()).toString();
                ConfigurationNode embedded = null;
                if (plugin.getResource(path) != null) {
                    embedded = loader.loadEmbeddedFile(path);
                    if (plugin.configBoolean(Option.LOOT_UPDATE_LOOT_TABLES)) {
                        // Merge embedded and user files if config should update
                        config = loader.mergeNodes(embedded, user);
                    }
                }
                // Load corresponding loot table type
                String fileName = lootTableFile.getName().replace(".yml", "");
                NamespacedId id = NamespacedId.fromDefault(fileName);

                LootTable lootTable = loadLootTable(id, lootTableFile, config);
                if (lootTable == null) continue;

                manager.addLootTable(id, lootTable);

                if (embedded != null) {
                    loader.saveConfigIfUpdated(lootTableFile, embedded, user, config);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Load loot tables registered from the API
        loadCustomLootTables();
        // Send info message
        int tablesLoaded = 0;
        int poolsLoaded = 0;
        int lootLoaded = 0;
        for (LootTable table : manager.getLootTables().values()) {
            for (LootPool pool : table.getPools()) {
                poolsLoaded++;
                lootLoaded += pool.getLoot().size();
            }
            tablesLoaded++;
        }
        plugin.logger().info("Loaded " + lootLoaded + " loot entries in " + poolsLoaded + " pools and " + tablesLoaded + " tables");
    }

    private LootTable loadLootTable(NamespacedId id, File file, ConfigurationNode config) {
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
                    String lootTypeName = lootNode.node("type").getString("");
                    LootType lootType = LootType.valueOf(lootTypeName.toUpperCase(Locale.ROOT));
                    // Item loot
                    LootParsingContext context = new LootParsingContextImpl(manager);
                    if (lootType == LootType.ITEM) {
                        // Ignore loot if below the ignore_below major version
                        int ignoreBelow = lootNode.node("ignore_below").getInt(-1);
                        if (ignoreBelow != -1 && !manager.getPlugin().isAtLeastVersion(ignoreBelow)) {
                            index++;
                            continue;
                        }

                        loot = getParser(lootType).parse(context, ApiConfigNode.toApi(lootNode));
                    } else if (lootType == LootType.COMMAND) { // Command loot
                        loot = getParser(lootType).parse(context, ApiConfigNode.toApi(lootNode));
                        // Entity loot, mainly for fishing
                    } else if (lootType == LootType.ENTITY) {
                        loot = getParser(lootType).parse(context, ApiConfigNode.toApi(lootNode));
                    } else {
                        // Parse custom loot registered from API
                        LootParser customParser = manager.getCustomLootParsers().get(lootTypeName);
                        if (customParser == null) {
                            throw new IllegalArgumentException("Unknown loot type: " + lootTypeName);
                        }

                        loot = customParser.parse(context, ApiConfigNode.toApi(lootNode));
                    }
                } catch (Exception e) {
                    manager.getPlugin().logger().warn("Error parsing loot in file loot/" + file.getName() + " at path pools." + poolName + ".loot." + index + ", see below for error:");
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

    private void generateDefaultLootTables() {
        File fishingFile = new File(plugin.getPluginFolder() + "/loot", "fishing.yml");
        if (!fishingFile.exists()) {
            plugin.saveResource("loot/fishing.yml", false);
        }
        File excavationFile = new File(plugin.getPluginFolder() + "/loot", "excavation.yml");
        if (!excavationFile.exists()) {
            plugin.saveResource("loot/excavation.yml", false);
        }
    }

    private void loadCustomLootTables() {
        var api = (ApiAuraSkills) plugin.getApi();
        for (NamespacedRegistry registry : api.getNamespacedRegistryMap().values()) {
            registry.getLootDirectory().ifPresent(dir -> loadExternalLootDir(dir, registry));
        }
    }

    private void loadExternalLootDir(File dir, NamespacedRegistry registry) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File lootTableFile : files) {
            if (!lootTableFile.isFile() || !lootTableFile.getName().endsWith(".yml")) {
                continue;
            }
            ConfigurateLoader loader = new ConfigurateLoader(plugin, TypeSerializerCollection.builder().build());
            try {
                // Load user file
                ConfigurationNode config = loader.loadUserFile(lootTableFile);

                String fileName = lootTableFile.getName().replace(".yml", "");
                NamespacedId id = NamespacedId.of(registry.getNamespace(), fileName);

                LootTable lootTable = loadLootTable(id, lootTableFile, config);
                if (lootTable == null) continue;

                manager.addLootTable(id, lootTable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
