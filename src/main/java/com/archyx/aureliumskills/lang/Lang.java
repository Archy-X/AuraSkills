package com.archyx.aureliumskills.lang;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Lang {

	private static final Map<MessageKey, String> messages = new HashMap<>();
	private static final Map<UnitMessage, String> units = new HashMap<>();
	public static String language;
	private final Plugin plugin;
	
	public Lang(Plugin plugin) {
		this.plugin = plugin;
		File file = new File(plugin.getDataFolder(), "messages_en.yml");
		if (!file.exists()) {
			plugin.saveResource("messages_en.yml", false);
		}
	}

	public void loadDefaultMessages() {
		//Loads default message from embedded resource
		InputStream inputStream = plugin.getResource("messages_en.yml");
		if (inputStream != null) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
			//Load units
			for (UnitMessage key : UnitMessage.values()) {
				String message = config.getString(key.getPath());
				if (message != null) {
					units.put(key, message.replace('&', '§'));
				}
			}
			for (MessageKey key : MessageKey.values()) {
				String message = config.getString(key.getPath());
				if (message != null) {
					messages.put(key, message
							.replace('&', '§')
							.replace("{mana_unit}", units.get(UnitMessage.MANA))
							.replace("{hp_unit}", units.get(UnitMessage.HP))
							.replace("{xp_unit}", units.get(UnitMessage.XP)));
				}
				else {
					Bukkit.getLogger().severe("[AureliumSkills] Message with path " + key.getPath() + " was null!");
				}
			}
		}
	}

	public void loadLanguages() {
		Bukkit.getLogger().info("[AureliumSkills] Loading languages...");
		long startTime = System.currentTimeMillis();
		//Sets default language
		language = plugin.getConfig().getString("default-language");
		//Load file
		try {
			File file = new File(plugin.getDataFolder(), "messages_" + language + ".yml");
			//Load and update file
			FileConfiguration config = updateFile(file, YamlConfiguration.loadConfiguration(file));
			//Load units
			for (UnitMessage key : UnitMessage.values()) {
				String message = config.getString(key.getPath());
				if (message != null) {
					units.put(key, message.replace('&', '§'));
				}
			}
			//Add message keys
			for (MessageKey key : MessageKey.values()) {
				String message = config.getString(key.getPath());
				if (message != null) {
					messages.put(key, message
							.replace('&', '§')
							.replace("{mana_unit}", units.get(UnitMessage.MANA))
							.replace("{hp_unit}", units.get(UnitMessage.HP))
							.replace("{xp_unit}", units.get(UnitMessage.XP)));
				}
				else {
					Bukkit.getLogger().severe("[AureliumSkills] Message with path " + key.getPath() + " was null!");
				}
			}
			long endTime = System.currentTimeMillis();
			Bukkit.getLogger().info("[AureliumSkills] Loaded languages in " + (endTime - startTime) + "ms");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}



	private FileConfiguration updateFile(File file, FileConfiguration config) {
		InputStream stream = plugin.getResource("messages_en.yml");
		if (stream != null) {
			int currentVersion = config.getInt("file_version");
			FileConfiguration imbConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
			int imbVersion = imbConfig.getInt("file_version");
			//If versions do not match
			if (currentVersion != imbVersion) {
				try {
					ConfigurationSection configSection = imbConfig.getConfigurationSection("");
					int keysAdded = 0;
					if (configSection != null) {
						for (String key : configSection.getKeys(true)) {
							if (!config.contains(key)) {
								config.set(key, imbConfig.get(key));
								keysAdded++;
							}
						}
					}
					config.save(file);
					Bukkit.getLogger().info("[AureliumSkills] messages_" + language + ".yml was updated to a new file version, " + keysAdded + " new keys were added.");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return YamlConfiguration.loadConfiguration(file);
	}
	
	public static String getMessage(MessageKey key) {
		String message = messages.get(key);
		if (message != null) {
			return message;
		}
		else {
			Bukkit.getLogger().warning("[AureliumSkills] Message key " + key + " with path " + key.getPath() + " was null!");
			return "";
		}
	}

}
