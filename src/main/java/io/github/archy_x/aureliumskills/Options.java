package io.github.archy_x.aureliumskills;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Options {

	private Plugin plugin;
	private Map<String, ChatColor> colors;
	
	public static boolean enable_action_bar;
	public static boolean enable_health_on_action_bar;
	public static boolean enable_mana_on_action_bar;
	public static boolean enable_roman_numerals;
	public static double skillLevelRequirementsMultiplier;
	public static double skillPointRewardMultiplier;
	public static ChatColor health_text_color;
	public static ChatColor mana_text_color;
	public static ChatColor skill_xp_text_color;
	public static ChatColor xp_progress_text_color;
	
	public Options(Plugin plugin) {
		this.plugin = plugin;
		colors = new HashMap<>();
		for (ChatColor color : ChatColor.values()) {
			if (color != ChatColor.BOLD && color != ChatColor.ITALIC && color != ChatColor.MAGIC && color != ChatColor.RESET 
					&& color != ChatColor.STRIKETHROUGH && color != ChatColor.UNDERLINE) {
				colors.put(color.name(), color);
			}
		}
	}
	
	public void loadConfig() {
		FileConfiguration config = plugin.getConfig();
		enable_action_bar = config.getBoolean("enable-action-bar", true);
		enable_health_on_action_bar = config.getBoolean("enable-health-on-action-bar", true);
		enable_mana_on_action_bar = config.getBoolean("enable-mana-on-action-bar", true);
		enable_roman_numerals = config.getBoolean("enable-roman-numerals", true);
		skillLevelRequirementsMultiplier = config.getDouble("skill-level-requirements-multiplier", 100);
		skillPointRewardMultiplier = config.getDouble("skill-point-reward-multiplier", 1.032);
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
	}
}
