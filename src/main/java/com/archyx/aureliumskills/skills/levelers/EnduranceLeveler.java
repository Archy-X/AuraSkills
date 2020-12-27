package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class EnduranceLeveler extends SkillLeveler {
	
	public EnduranceLeveler(AureliumSkills plugin) {
		super(plugin, Ability.RUNNER);
	}
	
	public void startTracking() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (OptionL.isEnabled(Skill.ENDURANCE)) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (!blockXpGain(player)) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							int xpAmount = 0;
							if (player.hasMetadata("skillsLastSprintDist")) {
								int sprintDist = player.getStatistic(Statistic.SPRINT_ONE_CM) - player.getMetadata("skillsLastSprintDist").get(0).asInt();
								if (sprintDist > 1000) {
									xpAmount += (sprintDist / 100) * getXp(player, Source.SPRINT_PER_METER);
									player.setMetadata("skillsLastSprintDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.SPRINT_ONE_CM)));
								}
							} else {
								player.setMetadata("skillsLastSprintDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.SPRINT_ONE_CM)));
							}
							if (player.hasMetadata("skillsLastWalkDist")) {
								int walkDist = player.getStatistic(Statistic.WALK_ONE_CM) - player.getMetadata("skillsLastWalkDist").get(0).asInt();
								if (walkDist > 100) {
									xpAmount += (walkDist / 100) * getXp(player, Source.WALK_PER_METER);
									player.setMetadata("skillsLastWalkDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.WALK_ONE_CM)));
								}
							} else {
								player.setMetadata("skillsLastWalkDist", new FixedMetadataValue(plugin, player.getStatistic(Statistic.WALK_ONE_CM)));
							}
							if (xpAmount > 0) {
								plugin.getLeveler().addXp(player, Skill.ENDURANCE, xpAmount);
							}
						}
					}
				}
			}
		}, 0L, 2400L);
	}
}
