package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Health implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		setHealth(event.getPlayer());
	}
	
	public static void reload(Player player) {
		setHealth(player);
	}
	
	private static void setHealth(Player player) {
		//Calculates the amount of health to add
		double modifier = ((double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.HEALTH)) * Options.getDoubleOption(Setting.HEALTH_MODIFIER);
		boolean hadModifier = false;
		//Removes existing modifiers of the same name
		for (AttributeModifier am : player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers()) {
			if (am.getName().equals("skillsHealth")) {
				player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(am);
				hadModifier = true;
			}
		}
		//Resets max health to move to new system
		if (hadModifier == false) {
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
		}
		//Applies modifier
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(new AttributeModifier("skillsHealth", modifier, Operation.ADD_NUMBER));
		//Applies health scaling
		if (Options.getBooleanOption(Setting.HEALTH_SCALING)) {
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
		else {
			player.setHealthScaled(false);
		}
	}
}
