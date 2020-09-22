package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Health implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		setHealth(event.getPlayer());
	}

	public static void reload(Player player) {
		if (player != null) {
			setHealth(player);
		}
	}

	@EventHandler
	public void worldChange(PlayerChangedWorldEvent event) {
		setHealth(event.getPlayer());
	}

	private static void setHealth(Player player) {
		//Calculates the amount of health to add
		PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
		if (playerStat != null) {
			double modifier = ((double) playerStat.getStatLevel(Stat.HEALTH)) * Options.getDoubleOption(Setting.HEALTH_MODIFIER);
			AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			if (attribute != null) {
				double originalMaxHealth = attribute.getValue();
				boolean hasChange = true;
				//Removes existing modifiers of the same name and check for change
				for (AttributeModifier am : attribute.getModifiers()) {
					if (am.getName().equals("skillsHealth")) {
						//Check for any changes, if not, return
						if (originalMaxHealth == originalMaxHealth - am.getAmount() + modifier) {
							hasChange = false;
						}
						//Removes if has change
						if (hasChange) {
							attribute.removeModifier(am);
						}
					}
				}
				//Disable health scaling if in disable world
				if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
					player.setHealthScaled(false);
					return;
				}
				//Return if no change
				if (hasChange) {
					//Applies modifier
					attribute.addModifier(new AttributeModifier("skillsHealth", modifier, Operation.ADD_NUMBER));
					//Sets health to max if over max
					if (player.getHealth() > attribute.getValue()) {
						player.setHealth(attribute.getValue());
					}
				}
				//Applies health scaling
				if (Options.getBooleanOption(Setting.HEALTH_SCALING)) {
					double health = attribute.getValue();
					if (health < 23) {
						player.setHealthScale(20.0);
						player.setHealthScaled(true);
					} else if (health < 28) {
						player.setHealthScale(22.0);
						player.setHealthScaled(true);
					} else if (health < 36) {
						player.setHealthScale(24.0);
						player.setHealthScaled(true);
					} else if (health < 49) {
						player.setHealthScale(26.0);
						player.setHealthScaled(true);
					} else if (health < 70) {
						player.setHealthScale(28.0);
						player.setHealthScaled(true);
					} else if (health < 104) {
						player.setHealthScale(30.0);
						player.setHealthScaled(true);
					} else if (health < 159) {
						player.setHealthScale(32.0);
						player.setHealthScaled(true);
					} else if (health < 248) {
						player.setHealthScale(34.0);
						player.setHealthScaled(true);
					} else if (health < 392) {
						player.setHealthScale(36.0);
						player.setHealthScaled(true);
					} else if (health < 625) {
						player.setHealthScale(38.0);
						player.setHealthScaled(true);
					} else {
						player.setHealthScale(40.0);
						player.setHealthScaled(true);
					}
				} else {
					player.setHealthScaled(false);
				}
			}
		}
	}
}
