package io.github.archy_x.aureliumskills.stats;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class Health implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		setHealth(event.getPlayer());
	}
	
	public static void reload(Player player) {
		setHealth(player);
	}
	
	private static void setHealth(Player player) {
		double modifier = ((double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.HEALTH)) / 2;
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0 + modifier);
		double health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		if (health < 23) {
			player.setHealthScale(20.0);
			player.setHealthScaled(true);
		}
		else if (health < 28) {
			player.setHealthScale(22.0);
			player.setHealthScaled(true);
		}
		else if (health < 36) {
			player.setHealthScale(24.0);
			player.setHealthScaled(true);
		}
		else if (health < 49) {
			player.setHealthScale(26.0);
			player.setHealthScaled(true);
		}
		else if (health < 70) {
			player.setHealthScale(28.0);
			player.setHealthScaled(true);
		}
		else if (health < 104) {
			player.setHealthScale(30.0);
			player.setHealthScaled(true);
		}
		else if (health < 159) {
			player.setHealthScale(32.0);
			player.setHealthScaled(true);
		}
		else if (health < 248) {
			player.setHealthScale(34.0);
			player.setHealthScaled(true);
		}
		else if (health < 392) {
			player.setHealthScale(36.0);
			player.setHealthScaled(true);
		}
		else if (health < 625) {
			player.setHealthScale(38.0);
			player.setHealthScaled(true);
		}
		else {
			player.setHealthScale(40.0);
			player.setHealthScaled(true);
		}
	}
}
