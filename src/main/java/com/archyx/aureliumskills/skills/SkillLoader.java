package com.archyx.aureliumskills.skills;

import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.stats.PlayerStat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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
	
	private final File file;
	private final FileConfiguration config;
	private final Plugin plugin;

	public SkillLoader(File file, FileConfiguration config, Plugin plugin) {
		this.file = file;
		this.config = config;
		this.plugin = plugin;
	}
	
	public void loadSkillData() {
		Bukkit.getLogger().info("[AureliumSkills] Loading Skill Data...");
		long startTime = System.currentTimeMillis();
		int playersLoaded = 0;
		ConfigurationSection configurationSection = config.getConfigurationSection("skillData");
		if (configurationSection != null) {
			for (String stringId : configurationSection.getKeys(false)) {
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
		Bukkit.getLogger().info("[AureliumSkills] Loaded " + playersLoaded + " Player Skill Data in " + (endTime - startTime) + "ms");
	}
	
	public void saveSkillData(boolean silent) {
		if (!silent) {
			Bukkit.getLogger().info("[AureliumSkills] Saving Skill Data...");
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
				Bukkit.getLogger().info("[AureliumSkills] Skill Data Saved!");
			}
		}
		catch (IOException e) {
			if (!silent) {
				Bukkit.getLogger().severe("[AureliumSkills] An error occurred while trying to save skill data!");
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
