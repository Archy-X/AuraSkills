package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class EnduranceLeveler {

	private Plugin plugin;
	
	public EnduranceLeveler(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void startTracking() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						int xpAmount = 0;
						if (player.hasMetadata("skillsLastSprintDist")) {
							int sprintDist = player.getStatistic(Statistic.SPRINT_ONE_CM) - player.getMetadata("skillsLastSprintDist").get(0).asInt();
							if (sprintDist > 1000) {
	    						xpAmount += sprintDist / 300;
	    						player.setMetadata("skillsLastSprintDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.SPRINT_ONE_CM)));
							}
						}
						else {
							player.setMetadata("skillsLastSprintDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.SPRINT_ONE_CM)));
						}
						if (player.hasMetadata("skillsLastWalkDist")) {
							int walkDist = player.getStatistic(Statistic.WALK_ONE_CM) - player.getMetadata("skillsLastWalkDist").get(0).asInt();
							if (walkDist > 100) {
								xpAmount += walkDist / 1000;
	    						player.setMetadata("skillsLastWalkDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.WALK_ONE_CM)));
							}
						}
						else {
							player.setMetadata("skillsLastWalkDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.WALK_ONE_CM)));
						}
						if (xpAmount > 0) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ENDURANCE, xpAmount);
    						Leveler.playSound(player);
    						Leveler.checkLevelUp(player, Skill.ENDURANCE);
    						Leveler.sendActionBarMessage(player, Skill.ENDURANCE, xpAmount);
						}
					}
				}
			}
		}, 0L, 1200L);
	}
}
