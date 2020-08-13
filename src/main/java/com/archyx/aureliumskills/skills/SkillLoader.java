package com.archyx.aureliumskills.skills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.stats.PlayerStat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class SkillLoader {

	public static HashMap<UUID, PlayerSkill> playerSkills = new HashMap<UUID, PlayerSkill>();
	public static HashMap<UUID, PlayerStat> playerStats = new HashMap<UUID, PlayerStat>();
	
	private File file;
	private FileConfiguration config;
	private Plugin plugin;

	public SkillLoader(File file, FileConfiguration config, Plugin plugin) {
		this.file = file;
		this.config = config;
		this.plugin = plugin;
	}
	
	public void loadSkillData() {
		Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + "Loading Skill Data...");
		long startTime = System.currentTimeMillis();
		int playersLoaded = 0;
		if (config.getConfigurationSection("skillData") != null) {
			for (String stringId : config.getConfigurationSection("skillData").getKeys(false)) {
				UUID id = UUID.fromString(stringId);
				String name = config.getString("skillData." + stringId + ".name", stringId);
				PlayerSkill playerSkill = new PlayerSkill(id, name);
				PlayerStat playerStat = new PlayerStat(id);
				//Loading skill and stat data
				if (config.getConfigurationSection("skillData." + stringId + ".skills") != null) {
					for (String skillName : config.getConfigurationSection("skillData." + stringId + ".skills").getKeys(false)) {
						String skillData = config.getString("skillData." + stringId + ".skills." + skillName);
						String[] skillDataArray = skillData.split(":");
						int level = Integer.parseInt(skillDataArray[0]);
						double xp = 0.0;
						if (skillDataArray.length >= 2) {
							xp = Double.parseDouble(skillDataArray[1].replace(",", "."));
						}
						Skill skill = Skill.valueOf(skillName.toUpperCase());
						playerSkill.setSkillLevel(skill, level);
						playerSkill.setXp(skill, xp);

						for (int i = 0; i < skill.getAbilities().length; i++) {
							playerSkill.setAbilityLevel(skill.getAbilities()[i], (level + 3 - i) / 5);
						}

						playerStat.addStatLevel(skill.getPrimaryStat(), level - 1);
						playerStat.addStatLevel(skill.getSecondaryStat(), level / 2);
					}
				}
				playerSkills.put(id, playerSkill);
				playerStats.put(id, playerStat);
				playersLoaded++;
			}
		}
		long endTime = System.currentTimeMillis();
		Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.AQUA + "Loaded " + ChatColor.GOLD + playersLoaded + ChatColor.AQUA + " Player Skill Data in " + ChatColor.GOLD + (endTime - startTime) + "ms");
	}
	
	public void saveSkillData(boolean silent) {
		if (!silent) {
			Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + "Saving Skill Data...");
		}
		for (UUID id : playerSkills.keySet()) {
			PlayerSkill playerSkill = playerSkills.get(id);
			config.set("skillData." + id.toString() + ".name", playerSkill.getPlayerName());
			//Saving skills
			for (Skill skill : playerSkill.getSkillSet()) {
				int level = playerSkill.getSkillLevel(skill);
				double xp = playerSkill.getXp(skill);
				config.set("skillData." + id.toString() + ".skills." + skill, level + ":" + ((double) Math.round(xp * 1000) / 1000));
			}
		}
		try {
			config.save(file);
			if (!silent) {
				Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.AQUA + "Skill Data Saved!");
			}
		}
		catch (IOException e) {
			if (!silent) {
				Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.RED + "An error occured while trying to save skill data!");
			}
		}
	}

	public void startSaving() {
		new BukkitRunnable() {
			@Override
			public void run() {
				saveSkillData(true);
			}
		}.runTaskTimer(plugin, Options.dataSavePeriod, Options.dataSavePeriod);
	}
	
}
