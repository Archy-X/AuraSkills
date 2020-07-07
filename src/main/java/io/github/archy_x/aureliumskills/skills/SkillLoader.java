package io.github.archy_x.aureliumskills.skills;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.skills.abilities.Ability;
import io.github.archy_x.aureliumskills.stats.PlayerStat;

public class SkillLoader {

	public static HashMap<UUID, PlayerSkill> playerSkills = new HashMap<UUID, PlayerSkill>();
	public static HashMap<UUID, PlayerStat> playerStats = new HashMap<UUID, PlayerStat>();
	
	private File file;
	private FileConfiguration config;
	
	public SkillLoader(File file, FileConfiguration config) {
		this.file = file;
		this.config = config;
	}
	
	public void loadSkillData() {
		Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + "Loading Skill Data...");
		long startTime = System.currentTimeMillis();
		int playersLoaded = 0;
		if (config.getConfigurationSection("skillData") != null) {
			if (config.getConfigurationSection("skillData").getKeys(false) != null) {
				for (String stringId : config.getConfigurationSection("skillData").getKeys(false)) {
					UUID id = UUID.fromString(stringId);
					PlayerSkill playerSkill = new PlayerSkill(id);
					PlayerStat playerStat = new PlayerStat(id);
					//Loading skill and stat data
					if (config.getConfigurationSection("skillData." + stringId + ".skills") != null) {
						if (config.getConfigurationSection("skillData." + stringId + ".skills").getKeys(false) != null) {
							for (String skillName : config.getConfigurationSection("skillData." + stringId + ".skills").getKeys(false)) {
								String skillData = config.getString("skillData." + stringId + ".skills." + skillName);
								String[] skillDataArray = skillData.split(":");
								int level = Integer.parseInt(skillDataArray[0]);
								double xp = 0.0;
								if (skillDataArray.length >= 2) {
									xp = Double.parseDouble(skillDataArray[1]);
								}
								int skillPoints = 0;
								if (skillDataArray.length >= 3) {
									skillPoints = Integer.parseInt(skillDataArray[2]);
								}
								Skill skill = Skill.valueOf(skillName.toUpperCase());
								playerSkill.setSkillLevel(skill, level);
								playerSkill.setXp(skill, xp);
								playerSkill.setSkillPoints(skill, skillPoints);
								
								playerStat.addStatLevel(skill.getPrimaryStat(), level - 1);
								playerStat.addStatLevel(skill.getSecondaryStat(), level / 2);
							}
						}
					}
					//Load ability data
					if (config.getConfigurationSection("skillData." + stringId + ".abilities") != null) {
						if (config.getConfigurationSection("skillData." + stringId + ".abilities").getKeys(false) != null) {
							for (String abilityName : config.getConfigurationSection("skillData." + stringId + ".abilities").getKeys(false)) {
								int level = config.getInt("skillData." + stringId + ".abilities." + abilityName);
								Ability ability = Ability.valueOf(abilityName.toUpperCase());
								playerSkill.setAbilityLevel(ability, level);
							}
						}
					}
					playerSkills.put(id, playerSkill);
					playerStats.put(id, playerStat);
					playersLoaded++;
				}
			}
		}
		long endTime = System.currentTimeMillis();
		Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.AQUA + "Loaded " + ChatColor.GOLD + playersLoaded + ChatColor.AQUA + " Player Skill Data in " + ChatColor.GOLD + (endTime - startTime) + "ms");
	}
	
	public void saveSkillData() {
		Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + "Saving Skill Data...");
		for (UUID id : playerSkills.keySet()) {
			PlayerSkill playerSkill = playerSkills.get(id);
			//Saving skills
			for (Skill skill : playerSkill.getSkillSet()) {
				int level = playerSkill.getSkillLevel(skill);
				double xp = playerSkill.getXp(skill);
				int skillPoints = playerSkill.getSkillPoints(skill);
				NumberFormat nf = new DecimalFormat("##.###");
				config.set("skillData." + id.toString() + ".skills." + skill, level + ":" + nf.format(xp) + ":" + skillPoints);
			}
			//Saving abilities
			for (Ability ability : Ability.values()) {
				int level = playerSkill.getAbilityLevel(ability);
				config.set("skillData." + id.toString() + ".abilities." + ability, level);
			}
		}
		try {
			config.save(file);
		}
		catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.RED + "An error occured while trying to save town data!");
		}
	}
	
}
