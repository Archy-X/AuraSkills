package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.plugin.Plugin;

public class Regeneration implements Listener {

	private final Plugin plugin;
	
	public Regeneration(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRegen(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getRegainReason().equals(RegainReason.SATIATED)) {
				Player player = (Player) event.getEntity();
				//Check for disabled world
				if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
					return;
				}
				if (!Options.getBooleanOption(Setting.CUSTOM_REGEN_MECHANICS)) {
					if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
						PlayerStat stat = SkillLoader.playerStats.get(player.getUniqueId());
						if (player.getSaturation() > 0) {
							event.setAmount(event.getAmount() + (stat.getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.SATURATED_MODIFIER)));
						}
						else if (player.getFoodLevel() == 20) {
							event.setAmount(event.getAmount() + (stat.getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.HUNGER_FULL_MODIFIER)));
						}
						else if (player.getFoodLevel() >= 14) {
							event.setAmount(event.getAmount() + (stat.getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.HUNGER_ALMOST_FULL_MODIFIER)));
						}
					}
				}
				else {
					event.setCancelled(true);
				}
			}
		}
	}
	
	public void startRegen() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (Options.getBooleanOption(Setting.CUSTOM_REGEN_MECHANICS)) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					//Check for disabled world
					if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
						return;
					}
					if (!player.isDead()) {
						AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
						if (attribute != null) {
							if (player.getHealth() < attribute.getValue()) {
								if (player.getFoodLevel() >= 14 && player.getFoodLevel() < 20) {
									player.setHealth(Math.min((player.getHealth() + Options.getDoubleOption(Setting.BASE_REGEN) + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.HUNGER_ALMOST_FULL_MODIFIER)), attribute.getValue()));
									if (player.getFoodLevel() - 1 >= 0) {
										player.setFoodLevel(player.getFoodLevel() - 1);
									}
								} else if (player.getFoodLevel() == 20 && player.getSaturation() == 0) {
									player.setHealth(Math.min((player.getHealth() + Options.getDoubleOption(Setting.BASE_REGEN) + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.HUNGER_FULL_MODIFIER)), attribute.getValue()));
									if (player.getFoodLevel() - 1 >= 0) {
										player.setFoodLevel(player.getFoodLevel() - 1);
									}
								}
							}
						}
					}
				}
			}
		}, 0L, (long) Options.getDoubleOption(Setting.HUNGER_DELAY));
	}
	
	public void startSaturationRegen() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (Options.getBooleanOption(Setting.CUSTOM_REGEN_MECHANICS)) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					//Check for disabled world
					if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
						return;
					}
					if (!player.isDead()) {
						if (player.getSaturation() > 0 && player.getFoodLevel() >= 20) {
							AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
							if (attribute != null) {
								player.setHealth(Math.min((player.getHealth() + Options.getDoubleOption(Setting.BASE_REGEN) + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.SATURATED_MODIFIER)), attribute.getValue()));
							}
						}
					}
				}
			}
		}, 0L, (long) Options.getDoubleOption(Setting.SATURATED_DELAY));
	}
	
}
