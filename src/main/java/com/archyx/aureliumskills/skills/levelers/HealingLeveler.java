package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.GameMode;
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
		if (OptionL.isEnabled(Skill.HEALING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.HEALING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			//Checks if in blocked world
			if (AureliumSkills.worldManager.isInBlockedWorld(event.getPlayer().getLocation())) {
				return;
			}
			//Checks if in blocked region
			if (AureliumSkills.worldGuardEnabled) {
				if (AureliumSkills.worldGuardSupport.isInBlockedRegion(event.getPlayer().getLocation())) {
					return;
				}
			}
			//Check for permission
			if (!event.getPlayer().hasPermission("aureliumskills.healing")) {
				return;
			}
			//Check creative mode disable
			if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
				if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
					return;
				}
			}
			if (event.getItem().getType().equals(Material.POTION)) {
				if (event.getItem().getItemMeta() instanceof PotionMeta) {
					PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
					PotionData data = meta.getBasePotionData();
					Skill s = Skill.HEALING;
					if (!data.getType().equals(PotionType.MUNDANE) && !data.getType().equals(PotionType.THICK)
							&& !data.getType().equals(PotionType.WATER) && !data.getType().equals(PotionType.AWKWARD)) {
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
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onThrow(PotionSplashEvent event) {
		if (OptionL.isEnabled(Skill.HEALING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.HEALING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			if (event.getPotion().getEffects().size() > 0) {
				if (event.getEntity().getShooter() instanceof Player) {
					if (event.getPotion().getItem().getItemMeta() instanceof PotionMeta) {
						Player p = (Player) event.getEntity().getShooter();
						PotionMeta meta = (PotionMeta) event.getPotion().getItem().getItemMeta();
						PotionData data = meta.getBasePotionData();
						Skill s = Skill.HEALING;
						//Check for permission
						if (!p.hasPermission("aureliumskills.healing")) {
							return;
						}
						//Check creative mode disable
						if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
							if (p.getGameMode().equals(GameMode.CREATIVE)) {
								return;
							}
						}
						if (!data.getType().equals(PotionType.MUNDANE) && !data.getType().equals(PotionType.THICK)
								&& !data.getType().equals(PotionType.WATER) && !data.getType().equals(PotionType.AWKWARD)) {
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
