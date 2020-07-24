package io.github.archy_x.aureliumskills.skills.levelers;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.Source;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class HealingLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onConsume(PlayerItemConsumeEvent event) {
		if (Options.isEnabled(Skill.HEALING)) {
			if (event.isCancelled() == false) {
				//Checks if in blocked region
				if (AureliumSkills.worldGuardEnabled) {
					if (AureliumSkills.worldGuardSupport.isInBlockedRegion(event.getPlayer().getLocation())) {
						return;
					}
				}
				if (event.getItem().getType().equals(Material.POTION)) {
					if (event.getItem().getItemMeta() instanceof PotionMeta) {
						PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
						PotionData data = meta.getBasePotionData();
						Skill s = Skill.HEALING;
						if (data.getType().equals(PotionType.MUNDANE) == false && data.getType().equals(PotionType.THICK) == false
								&& data.getType().equals(PotionType.WATER) == false && data.getType().equals(PotionType.AWKWARD) == false) {
							Player p = event.getPlayer();
							if (data.isExtended()) {
								Leveler.addXp(p, s, Source.DRINK_EXTENDED);
							}
							else if (data.isUpgraded()) {
								Leveler.addXp(p, s, Source.DRINK_UPGRADED);
							}
							else {
								Leveler.addXp(p, s, Source.DRINK_REGULAR);
							}
						}
						
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onThrow(PotionSplashEvent event) {
		if (Options.isEnabled(Skill.HEALING)) {
			if (event.isCancelled() == false) {
				if (event.getPotion().getEffects().size() > 0) {
					if (event.getEntity().getShooter() instanceof Player) {
						if (event.getPotion().getItem().getItemMeta() instanceof PotionMeta) {
							Player p = (Player) event.getEntity().getShooter();
							PotionMeta meta = (PotionMeta) event.getPotion().getItem().getItemMeta();
							PotionData data = meta.getBasePotionData();
							Skill s = Skill.HEALING;
							if (data.getType().equals(PotionType.MUNDANE) == false && data.getType().equals(PotionType.THICK) == false
									&& data.getType().equals(PotionType.WATER) == false && data.getType().equals(PotionType.AWKWARD) == false) {
								if (data.isExtended()) {
									Leveler.addXp(p, s, Source.SPLASH_EXTENDED);
								}
								else if (data.isUpgraded()) {
									Leveler.addXp(p, s, Source.SPLASH_UPGRADED);
								}
								else {
									Leveler.addXp(p, s, Source.SPLASH_REGULAR);
								}
							}
						}
					}
				}
			}
		}
	}
	
}
