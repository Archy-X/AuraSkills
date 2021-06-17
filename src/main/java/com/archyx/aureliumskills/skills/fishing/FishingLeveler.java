package com.archyx.aureliumskills.skills.fishing;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.leveler.Leveler;
import com.archyx.aureliumskills.leveler.SkillLeveler;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

public class FishingLeveler extends SkillLeveler implements Listener {

	public FishingLeveler(AureliumSkills plugin) {
		super(plugin, Ability.FISHER);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFish(PlayerFishEvent event) {
		if (OptionL.isEnabled(Skills.FISHING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.FISHING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			if (event.getState().equals(State.CAUGHT_FISH)) {
				Player p = event.getPlayer();
				if (blockXpGain(p)) return;
				Skill s = Skills.FISHING;
				if (event.getCaught() instanceof Item) {
					ItemStack item = ((Item) event.getCaught()).getItemStack();
					Material mat = item.getType();
					Leveler leveler = plugin.getLeveler();
					if (XMaterial.isNewVersion()) {
						if (mat.equals(XMaterial.COD.parseMaterial())) {
							leveler.addXp(p, s, getXp(p, FishingSource.COD));
						} else if (mat.equals(XMaterial.SALMON.parseMaterial())) {
							leveler.addXp(p, s, getXp(p, FishingSource.SALMON));
						} else if (mat.equals(XMaterial.TROPICAL_FISH.parseMaterial())) {
							leveler.addXp(p, s, getXp(p, FishingSource.TROPICAL_FISH));
						} else if (mat.equals(XMaterial.PUFFERFISH.parseMaterial())) {
							leveler.addXp(p, s, getXp(p, FishingSource.PUFFERFISH));
						}
					} else if (mat.equals(XMaterial.COD.parseMaterial())) {
						switch (item.getDurability()) {
							case 0:
								leveler.addXp(p, s, getXp(p, FishingSource.COD));
								break;
							case 1:
								leveler.addXp(p, s, getXp(p, FishingSource.SALMON));
								break;
							case 2:
								leveler.addXp(p, s, getXp(p, FishingSource.TROPICAL_FISH));
								break;
							case 3:
								leveler.addXp(p, s, getXp(p, FishingSource.PUFFERFISH));
								break;
						}
					}
					if (mat.equals(Material.BOW) || mat.equals(Material.ENCHANTED_BOOK) || mat.equals(Material.NAME_TAG) || mat.equals(Material.SADDLE) || mat.equals(XMaterial.NAUTILUS_SHELL.parseMaterial())) {
						leveler.addXp(p, s, getXp(p, FishingSource.TREASURE));
					} else if (mat.equals(Material.BOWL) || mat.equals(Material.LEATHER) || mat.equals(Material.LEATHER_BOOTS) || mat.equals(Material.ROTTEN_FLESH)
							|| mat.equals(Material.POTION) || mat.equals(Material.BONE) || mat.equals(Material.TRIPWIRE_HOOK) || mat.equals(Material.STICK)
							|| mat.equals(Material.STRING) || mat.equals(XMaterial.INK_SAC.parseMaterial()) || mat.equals(XMaterial.LILY_PAD.parseMaterial())
							|| mat.equals(XMaterial.BAMBOO.parseMaterial())) {
						leveler.addXp(p, s, getXp(p, FishingSource.JUNK));
					} else if (mat.equals(Material.FISHING_ROD)) {
						if (item.getEnchantments().size() != 0) {
							leveler.addXp(p, s, getXp(p, FishingSource.TREASURE));
						} else {
							leveler.addXp(p, s, getXp(p, FishingSource.JUNK));
						}
					}
				}
			}
		}
	}
	
	
}
