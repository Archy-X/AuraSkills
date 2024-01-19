package dev.aurelium.auraskills.bukkit.loot;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.context.MobContextProvider;
import dev.aurelium.auraskills.bukkit.loot.context.SourceContextProvider;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.config.Option;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
	private final Map<Skill, LootTable> skillLootTables;
	private final Map<NamespacedId, LootTable> otherLootTables;
	
	public LootTableManager(AuraSkills plugin) {
		this.plugin = plugin;
		this.lootManager = new LootManager(plugin);
		this.skillLootTables = new HashMap<>();
		this.otherLootTables = new HashMap<>();
		initLootManager();
	}

	public void initLootManager() {
		lootManager.registerContextProvider(new SourceContextProvider(plugin));
		lootManager.registerContextProvider(new MobContextProvider());
		lootManager.registerCustomItemParser(new ItemKeyParser(plugin));
		lootManager.addLootOptionKeys("xp");
		lootManager.addPoolOptionKeys("chance_per_luck", "require_open_water");
	}

	@Nullable
	public LootTable getLootTable(Skill skill) {
		return skillLootTables.get(skill);
	}

	@Nullable
	public LootTable getLootTable(NamespacedId id) {
		return otherLootTables.get(id);
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

		skillLootTables.clear();
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
				LootTable lootTable = lootManager.getLootLoader().loadLootTable(lootTableFile, config);
				if (lootTable == null) continue;

				// Parse skill from file name
				String fileName = lootTableFile.getName().replace(".yml", "");
				Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(fileName));
				if (skill != null) {
					skillLootTables.put(skill, lootTable);
				} else {
					otherLootTables.put(NamespacedId.fromDefault(fileName), lootTable);
				}

				if (embedded != null) {
					plugin.config().saveConfigIfUpdated(lootTableFile, embedded, user, config);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Send info message
		int tablesLoaded = 0;
		int poolsLoaded = 0;
		int lootLoaded = 0;
		for (LootTable table : skillLootTables.values()) {
			for (LootPool pool : table.getPools()) {
				poolsLoaded++;
				lootLoaded += pool.getLoot().size();
			}
			tablesLoaded++;
		}
		for (LootTable table : otherLootTables.values()) {
			for (LootPool pool : table.getPools()) {
				poolsLoaded++;
				lootLoaded += pool.getLoot().size();
			}
			tablesLoaded++;
		}
		plugin.getLogger().info("Loaded " + lootLoaded + " loot entries in " + poolsLoaded + " pools and " + tablesLoaded + " tables");
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

}
