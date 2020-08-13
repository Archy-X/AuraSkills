package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.plugin.Plugin;

public class Regeneration implements Listener {

	private Plugin plugin;
	
	public Regeneration(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRegen(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getRegainReason().equals(RegainReason.SATIATED)) {
				if (!Options.getBooleanOption(Setting.CUSTOM_REGEN_MECHANICS)) {
					Player player = (Player) event.getEntity();
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
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (Options.getBooleanOption(Setting.CUSTOM_REGEN_MECHANICS)) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.isDead() == false) {
							if (player.getHealth() < player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
								if (player.getFoodLevel() >= 14 && player.getFoodLevel() < 20) {
									
									if ((player.getHealth() + Options.getDoubleOption(Setting.BASE_REGEN) + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.HUNGER_ALMOST_FULL_MODIFIER)) <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
										player.setHealth(player.getHealth() + Options.getDoubleOption(Setting.BASE_REGEN) + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.HUNGER_ALMOST_FULL_MODIFIER));
									}
									else {
										player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
									}
									if (player.getFoodLevel() - 1 >= 0) {
										player.setFoodLevel(player.getFoodLevel() - 1);
									}
								}
								else if (player.getFoodLevel() == 20 && player.getSaturation() == 0) {
									if ((player.getHealth() + Options.getDoubleOption(Setting.BASE_REGEN) + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.HUNGER_FULL_MODIFIER)) <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
										player.setHealth(player.getHealth() + Options.getDoubleOption(Setting.BASE_REGEN) + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.HUNGER_FULL_MODIFIER));
									}
									else {
										player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
									}
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
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (Options.getBooleanOption(Setting.CUSTOM_REGEN_MECHANICS)) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.isDead() == false) {
							if (player.getSaturation() > 0 && player.getFoodLevel() >= 20) {
								if ((player.getHealth() +  Options.getDoubleOption(Setting.BASE_REGEN) + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.SATURATED_MODIFIER)) <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
									player.setHealth(player.getHealth() + Options.getDoubleOption(Setting.BASE_REGEN) + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) * Options.getDoubleOption(Setting.SATURATED_MODIFIER));
								}
								else {
									player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
								}
							}
						}
					}
				}
			}
		}, 0L, (long) Options.getDoubleOption(Setting.SATURATED_DELAY));
	}
	
}
