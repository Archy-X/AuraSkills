package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.sources.HealingSource;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class HealingLeveler extends SkillLeveler implements Listener {

	public HealingLeveler(AureliumSkills plugin) {
		super(plugin, Ability.HEALER);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	@SuppressWarnings("deprecation")
	public void onConsume(PlayerItemConsumeEvent event) {
		if (OptionL.isEnabled(Skills.HEALING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.HEALING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			if (blockXpGain(event.getPlayer())) return;
			Player p = event.getPlayer();
			Skill s = Skills.HEALING;
			Leveler leveler = plugin.getLeveler();
			if (event.getItem().getType().equals(Material.POTION)) {
				if (event.getItem().getItemMeta() instanceof PotionMeta) {
					PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
					PotionData data = meta.getBasePotionData();
					if (!data.getType().equals(PotionType.MUNDANE) && !data.getType().equals(PotionType.THICK)
							&& !data.getType().equals(PotionType.WATER) && !data.getType().equals(PotionType.AWKWARD)) {
						if (data.isExtended()) {
							leveler.addXp(p, s, getXp(HealingSource.DRINK_EXTENDED));
						}
						else if (data.isUpgraded()) {
							leveler.addXp(p, s, getXp(HealingSource.DRINK_UPGRADED));
						}
						else {
							leveler.addXp(p, s, getXp(HealingSource.DRINK_REGULAR));
						}
					}

				}
			}
			else if (XMaterial.isNewVersion()) {
				if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
					leveler.addXp(p, s, getXp(HealingSource.GOLDEN_APPLE));
				}
				else if (event.getItem().getType().equals(XMaterial.ENCHANTED_GOLDEN_APPLE.parseMaterial())) {
					leveler.addXp(p, s, getXp(HealingSource.ENCHANTED_GOLDEN_APPLE));
				}
			}
			else {
				if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
					MaterialData materialData = event.getItem().getData();
					if (materialData != null) {
						if (materialData.getData() == 0) {
							leveler.addXp(p, s, getXp(HealingSource.GOLDEN_APPLE));
						}
						else if (materialData.getData() == 1) {
							leveler.addXp(p, s, getXp(HealingSource.ENCHANTED_GOLDEN_APPLE));
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onThrow(PotionSplashEvent event) {
		if (OptionL.isEnabled(Skills.HEALING)) {
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
						Skill s = Skills.HEALING;
						if (blockXpGain(p)) return;
						if (!data.getType().equals(PotionType.MUNDANE) && !data.getType().equals(PotionType.THICK)
								&& !data.getType().equals(PotionType.WATER) && !data.getType().equals(PotionType.AWKWARD)) {
							if (data.isExtended()) {
								plugin.getLeveler().addXp(p, s, getXp(HealingSource.SPLASH_EXTENDED));
							}
							else if (data.isUpgraded()) {
								plugin.getLeveler().addXp(p, s, getXp(HealingSource.SPLASH_UPGRADED));
							}
							else {
								plugin.getLeveler().addXp(p, s, getXp(HealingSource.SPLASH_REGULAR));
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
		if (!OptionL.isEnabled(Skills.HEALING)) return;
		// Check cancelled
		if (OptionL.getBoolean(Option.HEALING_CHECK_CANCELLED)) {
			if (event.isCancelled()) {
				return;
			}
		}
		if (event.getEntity().getEffects().size() == 0) return;
		if (!(event.getEntity().getShooter() instanceof Player)) return;
		if (!(event.getEntity().getItem().getItemMeta() instanceof PotionMeta)) return;

		Player player = (Player) event.getEntity().getShooter();
		PotionMeta meta = (PotionMeta) event.getEntity().getItem().getItemMeta();
		PotionData data = meta.getBasePotionData();

		Skill skill = Skills.HEALING;
		if (blockXpGain(player)) return;
		if (!data.getType().equals(PotionType.MUNDANE) && !data.getType().equals(PotionType.THICK)
				&& !data.getType().equals(PotionType.WATER) && !data.getType().equals(PotionType.AWKWARD)) {
			if (data.isExtended()) {
				plugin.getLeveler().addXp(player, skill, getXp(HealingSource.LINGERING_EXTENDED));
			}
			else if (data.isUpgraded()) {
				plugin.getLeveler().addXp(player, skill, getXp(HealingSource.LINGERING_UPGRADED));
			}
			else {
				plugin.getLeveler().addXp(player, skill, getXp(HealingSource.LINGERING_REGULAR));
			}
		}
	}

}
