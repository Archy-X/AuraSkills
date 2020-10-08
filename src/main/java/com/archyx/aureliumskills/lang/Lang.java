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

	private static final Map<Message, String> messages = new HashMap<>();
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
			for (Message message : Message.values()) {
				messages.put(message, config.getString(message.getPath().replace('&', '§')));
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
			//Add message keys
			for (Message message : Message.values()) {
				messages.put(message, config.getString(message.getPath().replace('&', '§')));
			}
			long endTime = System.currentTimeMillis();
			Bukkit.getLogger().info("[AureliumSkills] Loaded " + config.getStringList("languages").size() + " languages in " + (endTime - startTime) + "ms");
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
	
	public static String getMessage(Message message) {
		return messages.get(message);
	}

}
