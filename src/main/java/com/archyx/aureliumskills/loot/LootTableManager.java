package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.parser.BlockItemLootParser;
import com.archyx.aureliumskills.loot.parser.FishingItemLootParser;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.misc.DataUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTableManager {

	private final Map<Skill, LootTable> lootTables;
	private final AureliumSkills plugin;
	
	public LootTableManager(AureliumSkills plugin) {
		this.plugin = plugin;
		lootTables = new HashMap<>();
		generateDefaultLootTables();
		loadLootTables();
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
		File lootDirectory = new File(plugin.getDataFolder() + "/loot");
		if (!lootDirectory.exists()) {
			generateDefaultLootTables();
		}
		if (!lootDirectory.isDirectory()) return;

		File[] files = lootDirectory.listFiles();
		if (files == null) return;
		for (File lootTableFile : files) {
			if (!lootTableFile.isFile() || !lootTableFile.getName().endsWith(".yml")) {
				continue;
			}
			// Parse skill from file name
			String skillName = lootTableFile.getName().replace(".yml", "");
			Skill skill = plugin.getSkillRegistry().getSkill(skillName);
			if (skill == null) return;

			FileConfiguration config = YamlConfiguration.loadConfiguration(lootTableFile);
			matchConfig(config, lootTableFile); // Try to update file
			// Load corresponding loot table type
			String type = config.getString("type", "block");
			LootTable lootTable = loadLootTable(config, type);
			if (lootTable != null) {
				lootTables.put(skill, lootTable);
			}
		}
	}

	private LootTable loadLootTable(FileConfiguration config, String lootTableType) {
		ConfigurationSection poolsSection = config.getConfigurationSection("pools");
		if (poolsSection == null) return null;
		List<LootPool> pools = new ArrayList<>();
		for (String poolName : poolsSection.getKeys(false)) {
			ConfigurationSection currentPool = poolsSection.getConfigurationSection(poolName);
			if (currentPool == null) continue;

			double baseChance = currentPool.getDouble("base_chance", 0.01);
			int selectionPriority = currentPool.getInt("selection_priority", 1);

			// Parse each loot entry
			List<Map<?,?>> lootMapList = currentPool.getMapList("loot");
			List<LootEntry> lootList = new ArrayList<>();
			for (Map<?, ?> lootEntryMap : lootMapList) {
				String type = DataUtil.getString(lootEntryMap, "type");
				LootEntry loot = null;
				if (type.equalsIgnoreCase("item")) {
					if (lootTableType.equals("fishing")) {
						loot = new FishingItemLootParser(plugin).parse(lootEntryMap);
					} else if (lootTableType.equals("block")) {
						loot = new BlockItemLootParser(plugin).parse(lootEntryMap);
					}
				}
				if (loot != null) {
					lootList.add(loot);
				}
			}
			// Create pool
			LootPool pool = new LootPool(poolName, lootList, baseChance, selectionPriority);
			pools.add(pool);
		}
		// Sort pools by selection priority
		pools.sort((pool1, pool2) -> pool2.getSelectionPriority() - pool1.getSelectionPriority());
		// Create table
		return new LootTable(pools);
	}

	@Nullable
	public LootTable getLootTable(Skill skill) {
		return lootTables.get(skill);
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
