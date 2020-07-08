package io.github.archy_x.aureliumskills;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Lang {

	private static Map<Message, String> defaultLang;
	private static Map<String, Map<Message, String>> customLang;
	
	private static String language;
	private Plugin plugin;
	
	public Lang(Plugin plugin) {
		this.plugin = plugin;
		defaultLang = new HashMap<>();
		customLang = new HashMap<>();
	}
	
	public void loadLanguages() {
		Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.AQUA + "Loading languages...");
		FileConfiguration config = plugin.getConfig();
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
	
	public boolean setLanguage(String lang) {
		FileConfiguration config = plugin.getConfig();
		if (config.getStringList("languages").contains(lang)) {
			language = lang;
			config.set("default-language", lang);
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
	
}
