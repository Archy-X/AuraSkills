package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class HealingLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onConsume(PlayerItemConsumeEvent event) {
		if (event.isCancelled() == false) {
			if (event.getItem().getType().equals(Material.POTION)) {
				if (event.getItem().getItemMeta() instanceof PotionMeta) {
					PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
					PotionData data = meta.getBasePotionData();
					if (data.getType().equals(PotionType.MUNDANE) == false && data.getType().equals(PotionType.THICK) == false
							&& data.getType().equals(PotionType.WATER) == false && data.getType().equals(PotionType.AWKWARD) == false) {
						Player player = event.getPlayer();
						if (data.isExtended() || data.isUpgraded()) {
							if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
	    						SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.HEALING, 15);
	    						Leveler.playSound(player);
	    						Leveler.checkLevelUp(player, Skill.HEALING);
	    						Leveler.sendActionBarMessage(player, Skill.HEALING, 15);
	    					}
						}
						else {
							if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
	    						SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.HEALING, 10);
	    						Leveler.playSound(player);
	    						Leveler.checkLevelUp(player, Skill.HEALING);
	    						Leveler.sendActionBarMessage(player, Skill.HEALING, 10);
	    					}
						}
					}
					
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onThrow(PotionSplashEvent event) {
		if (event.isCancelled() == false) {
			if (event.getPotion().getEffects().size() > 0) {
				if (event.getEntity().getShooter() instanceof Player) {
					Player player = (Player) event.getEntity().getShooter();
					double xpAdded = 0;
					for (PotionEffect pe : event.getPotion().getEffects()) {
						xpAdded += (pe.getAmplifier() + 1) * 2;
						xpAdded += (double) pe.getDuration() / 4800;
					}
					xpAdded *= 10;
					if (xpAdded > 0) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.HEALING, xpAdded);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.HEALING);
							Leveler.sendActionBarMessage(player, Skill.HEALING, xpAdded);
						}
					}
				}
			}
		}
	}
	
}
