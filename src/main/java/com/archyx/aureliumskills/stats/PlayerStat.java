package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStat {

	private final UUID playerId;
	private final AureliumSkills plugin;
	
	private final Map<Stat, Double> stats;
	private final Map<String, StatModifier> modifiers;

	public PlayerStat(UUID id, AureliumSkills plugin) {
		stats = new HashMap<>();
		modifiers = new HashMap<>();
		this.playerId = id;
		this.plugin = plugin;
		for (Stat stat : Stat.values()) {
			stats.put(stat, 0.0);
		}
	}
	
	public double getStatLevel(Stat stat) {
		return stats.get(stat);
	}
	
	public void addStatLevel(Stat stat, double level) {
		stats.put(stat, getStatLevel(stat) + level);
	}

	public void addStatLevel(Stat stat, int level) {
		stats.put(stat, getStatLevel(stat) + level);
	}
	
	public void setStatLevel(Stat stat, double level) {
		stats.put(stat, level);
	}
	
	public UUID getPlayerId() {
		return playerId;
	}

	/*
	Calculates the base stat level without modifiers
	 */
	public double getBaseStatLevel(Stat stat) {
		double level = 0;
		//Checks if player has profile
		if (SkillLoader.playerSkills.containsKey(playerId)) {
			//Gets profile
			PlayerSkill playerSkill = SkillLoader.playerSkills.get(playerId);
			//Finds skills that have that stat
			for (Skill skill : Skill.values()) {
				//Calculates primary
				if (skill.getPrimaryStat() == stat) {
					level += playerSkill.getSkillLevel(skill) - 1;
				}
				//Calculates secondary
				else if (skill.getSecondaryStat() == stat) {
					level += (double) playerSkill.getSkillLevel(skill) / 2;
				}
			}
		}
		return level;
	}

	/*
	Adds a stat modifier and reloads stats
	 */
	public void addModifier(StatModifier modifier) {
		//Removes if already existing
		if (modifiers.containsKey(modifier.getName())) {
			StatModifier oldModifier = modifiers.get(modifier.getName());
			if (oldModifier.getStat() == modifier.getStat() && oldModifier.getValue() == modifier.getValue()) {
				return;
			}
			removeModifier(modifier.getName());
		}
		//Adds the modifier
		modifiers.put(modifier.getName(), modifier);
		//Changes the stat level
		addStatLevel(modifier.getStat(), modifier.getValue());
		//Reloads stats
		if (Bukkit.getPlayer(playerId) != null) {
			if (modifier.getStat() == Stat.HEALTH) {
				plugin.getHealth().reload(Bukkit.getPlayer(playerId));
			}
			else if (modifier.getStat() == Stat.LUCK) {
				new Luck(plugin).reload(Bukkit.getPlayer(playerId));
			}
		}
	}

	public void addModifier(StatModifier modifier, boolean reload) {
		//Removes if already existing
		StatModifier oldModifier = modifiers.get(modifier.getName());
		if (oldModifier != null) {
			if (oldModifier.getStat() == modifier.getStat() && oldModifier.getValue() == modifier.getValue()) {
				return;
			}
			removeModifier(modifier.getName(), reload);
		}
		//Adds the modifier
		modifiers.put(modifier.getName(), modifier);
		//Changes the stat level
		addStatLevel(modifier.getStat(), modifier.getValue());
		//Reloads stats
		if (reload) {
			if (Bukkit.getPlayer(playerId) != null) {
				if (modifier.getStat() == Stat.HEALTH) {
					plugin.getHealth().reload(Bukkit.getPlayer(playerId));
				} else if (modifier.getStat() == Stat.LUCK) {
					new Luck(plugin).reload(Bukkit.getPlayer(playerId));
				}
			}
		}
	}

	/*
	Removes a stat modifier and reloads stats
	 */
	public boolean removeModifier(String name) {
		//Gets the modifier to remove
		StatModifier modifier = modifiers.get(name);
		if (modifier != null) {
			//Changes the stat level
			setStatLevel(modifier.getStat(), stats.get(modifier.getStat()) - modifier.getValue());
			//Removes the modifier
			modifiers.remove(name);
			//Reloads stats
			if (Bukkit.getPlayer(playerId) != null) {
				if (modifier.getStat() == Stat.HEALTH) {
					plugin.getHealth().reload(Bukkit.getPlayer(playerId));
				}
				else if (modifier.getStat() == Stat.LUCK) {
					new Luck(plugin).reload(Bukkit.getPlayer(playerId));
				}
			}
			return true;
		}
		return false;
	}

	public boolean removeModifier(String name, boolean reload) {
		//Gets the modifier to remove
		StatModifier modifier = modifiers.get(name);
		if (modifier != null) {
			//Changes the stat level
			setStatLevel(modifier.getStat(), stats.get(modifier.getStat()) - modifier.getValue());
			//Removes the modifier
			modifiers.remove(name);
			//Reloads stats
			if (Bukkit.getPlayer(playerId) != null) {
				if (reload) {
					if (modifier.getStat() == Stat.HEALTH) {
						plugin.getHealth().reload(Bukkit.getPlayer(playerId));
					} else if (modifier.getStat() == Stat.LUCK) {
						new Luck(plugin).reload(Bukkit.getPlayer(playerId));
					}
				}
			}
			return true;
		}
		return false;
	}

	public Map<String, StatModifier> getModifiers() {
		return modifiers;
	}

}
