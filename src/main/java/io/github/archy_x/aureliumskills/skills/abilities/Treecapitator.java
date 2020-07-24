package io.github.archy_x.aureliumskills.skills.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import io.github.archy_x.aureliumskills.AureliumSkills;

public class Treecapitator implements RightClickAbility {

	@Override
	public void start(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
		player.sendMessage(AureliumSkills.tag + ChatColor.GOLD + "Treecapitator Activated!");
	}

	@Override
	public void update(Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(Player player) {
		AureliumSkills.abilityManager.setCooldown(player.getUniqueId(), Ability.TREECAPITATOR, 200);
		player.sendMessage(AureliumSkills.tag + ChatColor.GOLD + "Treecapitator has worn off");
	}

}
