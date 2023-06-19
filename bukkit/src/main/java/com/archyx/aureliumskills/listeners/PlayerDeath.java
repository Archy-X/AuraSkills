package com.archyx.aureliumskills.listeners;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.skills.Skill;



public class PlayerDeath implements Listener {

	private final AureliumSkills plugin;

	public PlayerDeath(AureliumSkills plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (OptionL.getBoolean(Option.RESET_SKILLS_ON_DEATH) || OptionL.getBoolean(Option.RESET_XP_ON_DEATH)) {
			Player player = event.getEntity();
			PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
			if (playerData != null){
				for (Skill s : plugin.getSkillRegistry().getSkills()) {
					if (OptionL.getBoolean(Option.RESET_SKILLS_ON_DEATH)) {
						resetPlayerSkills(player, playerData, s);
					} else if (OptionL.getBoolean(Option.RESET_XP_ON_DEATH)) {
						resetPlayerXp(playerData, s);
					}
    			}
			}
		}
	}

	private void resetPlayerSkills(Player player, PlayerData playerData, Skill skill) {
		int oldLevel = playerData.getSkillLevel(skill);
		playerData.setSkillLevel(skill, 1);
		playerData.setSkillXp(skill, 0);
		plugin.getLeveler().updateStats(player);
		plugin.getLeveler().updatePermissions(player);
		plugin.getLeveler().applyRevertCommands(player, skill, oldLevel, 1);
	}

	private void resetPlayerXp(PlayerData playerData, Skill skill) {
		playerData.setSkillXp(skill, 0);
	}
}
