package com.archyx.aureliumskills.lang;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Lang {

	private static final Map<Message, String> defaultLang = new HashMap<>();
	private static final Map<String, Map<Message, String>> customLang = new HashMap<>();
	
	private static String language;
	private final File file;
	private FileConfiguration config;
	private final Plugin plugin;
	
	public Lang(Plugin plugin) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), "messages.yml");
		if (!file.exists()) {
			plugin.saveResource("messages.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	public void loadDefaultMessages() {
		//Loads default message from embedded resource
		InputStream inputStream = plugin.getResource("messages.yml");
		if (inputStream != null) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
			for (Message message : Message.values()) {
				defaultLang.put(message, config.getString("messages." + message.getPath() + ".EN"));
			}
		}
	}

	public void loadLanguages() {
		Bukkit.getLogger().info("[AureliumSkills] Loading languages...");
		long startTime = System.currentTimeMillis();
		//Reloads config
		config = YamlConfiguration.loadConfiguration(file);
		//Sets default language
		language = config.getString("default-language");
		//For every custom language
		for (String customLanguage : config.getStringList("languages")) {
			Map<Message, String> customMessages = new HashMap<>();
			//For every message
			for (Message message : Message.values()) {
				if (config.contains("messages." + message.getPath() + "." + customLanguage)) {
					customMessages.put(message, config.getString("messages." + message.getPath() + "." + customLanguage));
				}
			}
			customLang.put(customLanguage, customMessages);
		}
		long endTime = System.currentTimeMillis();
		long timeElapsed = endTime - startTime;
		Bukkit.getLogger().info("[AureliumSkills] Loaded " + config.getStringList("languages").size() + " languages in " + timeElapsed + "ms");
	}
	
	public void matchConfig() {
		config.options().copyDefaults(true);
		try {
			boolean changesMade = false;
			InputStream is = plugin.getResource(file.getName());
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
				}
			}
            config.save(file);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean setLanguage(String lang) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (config.getStringList("languages").contains(lang)) {
			language = lang;
			config.set("default-language", lang);
			try {
				config.save(file);
			} catch (IOException e) {
				Bukkit.getLogger().severe("[AureliumSkills] Error saving messages.yml!");
			}
			return true;
		}
		return false;
	}
	
	public static String getMessage(Message message) {
		if (customLang.containsKey(language)) {
			if (customLang.get(language).containsKey(message)) {
				return customLang.get(language).get(message);
			}
			else {
				return defaultLang.get(message);
			}
		}
		else if (defaultLang.containsKey(message)) {
			return defaultLang.get(message);
		}
		return "";
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
}
