package dev.aurelium.auraskills.bukkit.loot;

import dev.aurelium.auraskills.api.loot.LootPool;
import dev.aurelium.auraskills.api.loot.LootTable;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.context.MobContextProvider;
import dev.aurelium.auraskills.bukkit.loot.context.SourceContextProvider;
import dev.aurelium.auraskills.bukkit.loot.entity.VanillaEntityParser;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.api.ApiAuraSkills;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.config.Option;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LootTableManager {

	private final AuraSkills plugin;
	private final LootManager lootManager;
	private final Map<NamespacedId, LootTable> lootTables;
	
	public LootTableManager(AuraSkills plugin) {
		this.plugin = plugin;
		this.lootManager = new LootManager(plugin);
		this.lootTables = new HashMap<>();
		initLootManager();
	}

	public void initLootManager() {
		lootManager.registerContextProvider(new SourceContextProvider(plugin));
		lootManager.registerContextProvider(new MobContextProvider());
		lootManager.registerCustomItemParser(new ItemKeyParser(plugin));
		lootManager.registerCustomEntityParser(new VanillaEntityParser(plugin));
		lootManager.addLootOptionKeys("xp");
		lootManager.addPoolOptionKeys("chance_per_luck", "require_open_water");
	}

	public LootManager getLootManager() {
		return lootManager;
	}

	@Nullable
	public LootTable getLootTable(Skill skill) {
		return lootTables.get(skill.getId());
	}

	@Nullable
	public LootTable getLootTable(NamespacedId id) {
		return lootTables.get(id);
	}

	public void generateDefaultLootTables() {
		File fishingFile = new File(plugin.getDataFolder() + "/loot", "fishing.yml");
		if (!fishingFile.exists()) {
			plugin.saveResource("loot/fishing.yml", false);
		}
		File excavationFile = new File(plugin.getDataFolder() + "/loot", "excavation.yml");
		if (!excavationFile.exists()) {
			plugin.saveResource("loot/excavation.yml", false);
		}
	}

	public void loadLootTables() {
		// Check that new loot files do not exist yet for conversion
		boolean convertFishing = !new File(plugin.getDataFolder() + "/loot", "fishing.yml").exists();
		boolean convertExcavation = !new File(plugin.getDataFolder() + "/loot", "excavation.yml").exists();
		// Generate default loot files
		File lootDirectory = new File(plugin.getDataFolder() + "/loot");
		if (!lootDirectory.exists() || convertFishing || convertExcavation) {
			generateDefaultLootTables();
		}
		if (!lootDirectory.isDirectory()) return;

		lootTables.clear();
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

				LootTable lootTable = lootManager.getLootLoader().loadLootTable(id, lootTableFile, config);
				if (lootTable == null) continue;

				lootTables.put(id, lootTable);

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
		for (LootTable table : lootTables.values()) {
			for (LootPool pool : table.getPools()) {
				poolsLoaded++;
				lootLoaded += pool.getLoot().size();
			}
			tablesLoaded++;
		}
		plugin.getLogger().info("Loaded " + lootLoaded + " loot entries in " + poolsLoaded + " pools and " + tablesLoaded + " tables");
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

				LootTable lootTable = lootManager.getLootLoader().loadLootTable(id, lootTableFile, config);
				if (lootTable == null) continue;

				lootTables.put(id, lootTable);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void matchConfig(FileConfiguration config, File file) {
		config.options().copyDefaults(true);
		try {
			boolean changesMade = false;
			InputStream is = plugin.getResource("loot/" + file.getName());
			if (is != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
				ConfigurationSection configurationSection = defConfig.getConfigurationSection("");
				if (configurationSection != null) {
					for (String key : configurationSection.getKeys(true)) {
						if (!config.contains(key)) {
							config.set(key, defConfig.get(key));
							if (!changesMade) {
								changesMade = true;
							}
						}
					}
					if (changesMade) {
						config.save(file);
					}
				}
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public boolean toInventory(ItemStack held) {
		if (ItemUtils.hasTelekinesis(held)) {
			return true;
		}
		return plugin.configBoolean(Option.LOOT_DIRECTLY_TO_INVENTORY);
	}

}
