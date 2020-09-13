package com.archyx.aureliumskills.stats;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

public enum Stat {

	HEALTH(ChatColor.RED + "", "❤"), 
	STRENGTH(ChatColor.DARK_RED + "", "➽"), 
	REGENERATION(ChatColor.GOLD + "", "❥"), 
	LUCK(ChatColor.DARK_GREEN + "", "☘"), 
	WISDOM(ChatColor.BLUE + "", "✿"), 
	TOUGHNESS(ChatColor.DARK_PURPLE + "", "✦");
	
	private final String color;
	private final String symbol;
	private final String name;
	
	Stat(String color, String symbol) {
		this.name = this.toString().toLowerCase();
		this.color = color;
		this.symbol = symbol;
	}
	
	public String getDisplayName() {
		return color + StringUtils.capitalize(name);
	}
	
	public String getColor() {
		return color;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getName() {
		return name;
	}
	
}
