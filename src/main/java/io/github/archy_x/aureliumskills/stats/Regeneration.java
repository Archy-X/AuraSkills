package io.github.archy_x.aureliumskills.stats;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.plugin.Plugin;

import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class Regeneration implements Listener {

	private Plugin plugin;
	
	public Regeneration(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRegen(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getRegainReason().equals(RegainReason.SATIATED)) {
				event.setCancelled(true);
			}
		}
	}
	
	public void startRegen() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.isDead() == false) {
						if (player.getHealth() < player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
							if (player.getFoodLevel() >= 14 && player.getFoodLevel() < 20) {
								
								if ((player.getHealth() + 1 + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) / 40) <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
									player.setHealth(player.getHealth() + 1 + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) / 40);
								}
								else {
									player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
								}
								if (player.getFoodLevel() - 1 >= 0) {
									player.setFoodLevel(player.getFoodLevel() - 1);
								}
							}
							else if (player.getFoodLevel() == 20 && player.getSaturation() == 0) {
								if ((player.getHealth() + 1 + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) / 40) <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
									player.setHealth(player.getHealth() + 1 + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) / 40);
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
		}, 0L, 60L);
	}
	
	public void startSaturationRegen() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.isDead() == false) {
						if (player.getSaturation() > 0 && player.getFoodLevel() >= 20) {
							if ((player.getHealth() + 1 + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) / 20) <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
								player.setHealth(player.getHealth() + 1 + (double) SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.REGENERATION) / 20);
							}
							else {
								player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
							}
						}
					}
				}
			}
		}, 0L, 20L);
	}
	
}
