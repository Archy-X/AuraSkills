package com.archyx.aureliumskills.lang;

import com.archyx.aureliumskills.AureliumSkills;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

	private static Map<Message, String> defaultLang = new HashMap<>();
	private static Map<String, Map<Message, String>> customLang = new HashMap<>();
	
	private static String language;
	private File file;
	private FileConfiguration config;
	private Plugin plugin;
	
	public Lang(Plugin plugin) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), "messages.yml");
		if (!file.exists()) {
			plugin.saveResource("messages.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	public void loadLanguages() {
		Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.AQUA + "Loading languages...");
		config = YamlConfiguration.loadConfiguration(file);
		language = config.getString("default-language");
		for (Message message : Message.values()) {
			//Load default message
			defaultLang.put(message, message.getDefaultMessage());
		}
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
		Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.GREEN + "Loaded " + config.getStringList("languages").size() + " languages!");
	}
	
	public void matchConfig() {
		config.options().copyDefaults(true);
		try {
			boolean changesMade = false;
			InputStream is = plugin.getResource(file.getName());
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
            for (String key : defConfig.getConfigurationSection("").getKeys(true)) {
                if (!config.contains(key)) {
                	config.set(key, defConfig.get(key));
                	if (!changesMade) {
                		changesMade = true;
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
				Bukkit.getLogger().severe("Error saving messages.yml!");
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
		return null;
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
}
