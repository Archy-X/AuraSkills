package io.github.archy_x.aureliumskills;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.Source;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class Options {

	private Plugin plugin;
	private Map<String, ChatColor> colors;
	
	public static boolean enable_action_bar;
	public static boolean enable_health_on_action_bar;
	public static boolean enable_mana_on_action_bar;
	public static boolean enable_roman_numerals;
	public static double skillLevelRequirementsMultiplier;
	public static double skillPointRewardMultiplier;
	public static int actionBarUpdatePeriod;
	public static int dataSavePeriod;
	public static boolean checkBlockReplace;
	public static double manaModifier;
	public static ChatColor health_text_color;
	public static ChatColor mana_text_color;
	public static ChatColor skill_xp_text_color;
	public static ChatColor xp_progress_text_color;
	
	private static Map<Skill, Boolean> skillToggle = new HashMap<>();
	private static Map<Source, Double> xpAmounts = new HashMap<>();
	private static Map<Source, Double> defXpAmounts = new HashMap<>();
	
	private static Map<Setting, Double> doubleOptions = new HashMap<>();
	private static Map<Setting, Boolean> booleanOptions = new HashMap<>();
	
	public Options(Plugin plugin) {
		this.plugin = plugin;
		colors = new HashMap<>();
		for (ChatColor color : ChatColor.values()) {
			if (color != ChatColor.BOLD && color != ChatColor.ITALIC && color != ChatColor.MAGIC && color != ChatColor.RESET 
					&& color != ChatColor.STRIKETHROUGH && color != ChatColor.UNDERLINE) {
				colors.put(color.name(), color);
			}
		}
		setDefaultXpAmounts();
	}
	
	public static boolean isEnabled(Skill skill) {
		if (skillToggle.containsKey(skill)) {
			return skillToggle.get(skill);
		}
		return true;
	}
	
	public static double getDoubleOption(Setting setting) {
		if (doubleOptions.containsKey(setting)) {
			return doubleOptions.get(setting);
		}
		else {
			return setting.getDefaultDouble();
		}
	}
	
	public static boolean getBooleanOption(Setting setting) {
		if (booleanOptions.containsKey(setting)) {
			return booleanOptions.get(setting);
		}
		else {
			return setting.getDefaultBoolean();
		}
	}
	
	public void loadConfig() {
		FileConfiguration config = plugin.getConfig();
		loadPrefix(config);
		enable_action_bar = config.getBoolean("enable-action-bar", true);
		enable_health_on_action_bar = config.getBoolean("enable-health-on-action-bar", true);
		enable_mana_on_action_bar = config.getBoolean("enable-mana-on-action-bar", true);
		enable_roman_numerals = config.getBoolean("enable-roman-numerals", true);
		skillLevelRequirementsMultiplier = config.getDouble("skill-level-requirements-multiplier", 100);
		skillPointRewardMultiplier = config.getDouble("skill-point-reward-multiplier", 1.032);
		actionBarUpdatePeriod = config.getInt("action-bar-update-period", 2);
		dataSavePeriod = config.getInt("data-save-period", 6000);
		checkBlockReplace = config.getBoolean("check-block-replace", true);
		manaModifier = config.getDouble("regeneration.mana-modifier", 0.25);
		if (colors.containsKey(config.getString("health-text-color").toUpperCase())) {
			health_text_color = colors.get(config.getString("health-text-color").toUpperCase());
		}
		else {
			health_text_color = ChatColor.RED;
			Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Invalid health text color, using default value instead!");
		}
		if (colors.containsKey(config.getString("mana-text-color").toUpperCase())) {
			mana_text_color = colors.get(config.getString("mana-text-color").toUpperCase());
		}
		else {
			mana_text_color = ChatColor.AQUA;
			Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Invalid mana text color, using default value instead!");
		}
		if (colors.containsKey(config.getString("skill-xp-text-color").toUpperCase())) {
			skill_xp_text_color = colors.get(config.getString("skill-xp-text-color").toUpperCase());
		}
		else {
			skill_xp_text_color = ChatColor.GOLD;
			Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Invalid skill xp text color, using default value instead!");
		}
		if (colors.containsKey(config.getString("xp-progress-text-color").toUpperCase())) {
			xp_progress_text_color = colors.get(config.getString("xp-progress-text-color").toUpperCase());
		}
		else {
			xp_progress_text_color = ChatColor.GRAY;
			Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Invalid xp progress text color, using default value instead!");
		}
		for (Setting setting : Setting.values()) {
			if (setting.getType() == "double") {
				if (config.contains(setting.getPath())) {
					doubleOptions.put(setting, config.getDouble(setting.getPath(), setting.getDefaultDouble()));
				}
				else {
					doubleOptions.put(setting, setting.getDefaultDouble());
				}
			}
			else if (setting.getType() == "boolean") {
				if (config.contains(setting.getPath())) {
					booleanOptions.put(setting, config.getBoolean(setting.getPath(), setting.getDefaultBoolean()));
				}
				else {
					booleanOptions.put(setting, setting.getDefaultBoolean());
				}
			}
		}
		
		for (Skill skill : Skill.values()) {
			if (config.contains(skill.toString().toLowerCase() + ".enabled")) {
				skillToggle.put(skill, config.getBoolean(skill.toString().toLowerCase() + ".enabled", true));
			}
			else {
				skillToggle.put(skill, true);
			}
		}
		
		for (Source type : Source.values()) {
			if (config.contains(type.getPath())) {
				if (config.getDouble(type.getPath()) >= 0) {
					xpAmounts.put(type, config.getDouble(type.getPath(), defXpAmounts.get(type)));
				}
				else {
					Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Invalid value in path " + type.getPath() + " in config, using default value instead.");
				}
			}
			else {
				Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Missing path " + type.getPath() + " in config, using default value instead.");
			}
		}
		
	}
	
	public void setDefaultXpAmounts() {
		for (Source type : Source.values()) {
			defXpAmounts.put(type, type.getDefault());
		}
	}
	
	public static double getXpAmount(Source key) {
		if (xpAmounts.containsKey(key)) {
			return xpAmounts.get(key);
		}
		else if (defXpAmounts.containsKey(key)) {
			return defXpAmounts.get(key);
		}
		return 0;
	}

	private void loadPrefix(FileConfiguration config) {
		//Load prefix color
		ChatColor prefixColor;
		try {
			prefixColor = ChatColor.valueOf(config.getString("prefix-bracket-color"));
		}
		catch (IllegalArgumentException e) {
			prefixColor = ChatColor.DARK_GRAY;
			Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Invalid prefix-bracket-color! Using the default instead!");
		}
		//Load and set prefix text
		String prefixText = config.getString("prefix-text", "&bSkills").replace("&", "§");
		AureliumSkills.tag = prefixColor + "[" + prefixText + prefixColor + "] " + ChatColor.RESET;
	}
}
