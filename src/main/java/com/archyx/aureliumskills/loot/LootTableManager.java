package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
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
			LootTable lootTable;
			if (type.equals("fishing")) {
				lootTable = loadFishingLootTable(config);
			} else {
				lootTable = loadBlockLootTable(config);
			}
			lootTables.put(skill, lootTable);
		}
	}

	private LootTable loadFishingLootTable(FileConfiguration config) {
		// TODO Fishing loot table loading
		return null;
	}

	private LootTable loadBlockLootTable(FileConfiguration config) {
		// TODO Block loot table loading
		return null;
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
