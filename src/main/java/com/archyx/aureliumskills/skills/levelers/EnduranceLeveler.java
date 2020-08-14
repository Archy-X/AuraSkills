package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.EnduranceAbilities;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class EnduranceLeveler {

	private final Plugin plugin;
	
	public EnduranceLeveler(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void startTracking() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (Options.isEnabled(Skill.ENDURANCE)) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					//Checks if in blocked world
					if (AureliumSkills.worldManager.isInBlockedWorld(player.getLocation())) {
						return;
					}
					//Checks if in blocked region
					if (AureliumSkills.worldGuardEnabled) {
						if (AureliumSkills.worldGuardSupport.isInBlockedRegion(player.getLocation())) {
							return;
						}
					}
					//Check for permission
					if (!player.hasPermission("aureliumskills.endurance")) {
						return;
					}
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						int xpAmount = 0;
						if (player.hasMetadata("skillsLastSprintDist")) {
							int sprintDist = player.getStatistic(Statistic.SPRINT_ONE_CM) - player.getMetadata("skillsLastSprintDist").get(0).asInt();
							if (sprintDist > 1000) {
								xpAmount += (sprintDist / 100) * EnduranceAbilities.getModifiedXp(player, Source.SPRINT_PER_METER);
								player.setMetadata("skillsLastSprintDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.SPRINT_ONE_CM)));
							}
						}
						else {
							player.setMetadata("skillsLastSprintDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.SPRINT_ONE_CM)));
						}
						if (player.hasMetadata("skillsLastWalkDist")) {
							int walkDist = player.getStatistic(Statistic.WALK_ONE_CM) - player.getMetadata("skillsLastWalkDist").get(0).asInt();
							if (walkDist > 100) {
								xpAmount += (walkDist / 100) * EnduranceAbilities.getModifiedXp(player, Source.WALK_PER_METER);
								player.setMetadata("skillsLastWalkDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.WALK_ONE_CM)));
							}
						}
						else {
							player.setMetadata("skillsLastWalkDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.WALK_ONE_CM)));
						}
						if (xpAmount > 0) {
							Leveler.addXp(player, Skill.ENDURANCE, xpAmount);
						}
					}
				}
			}
		}, 0L, 1200L);
	}
}
