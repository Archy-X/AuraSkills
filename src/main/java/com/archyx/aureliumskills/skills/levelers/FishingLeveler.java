package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.FishingAbilities;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

public class FishingLeveler implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFish(PlayerFishEvent event) {
		if (OptionL.isEnabled(Skill.FISHING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.FISHING_CHECK_CANCELLED)) {
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
			if (event.getState().equals(State.CAUGHT_FISH)) {
				Player p = event.getPlayer();
				//Check for permission
				if (!p.hasPermission("aureliumskills.fishing")) {
					return;
				}
				//Check creative mode disable
				if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
					if (p.getGameMode().equals(GameMode.CREATIVE)) {
						return;
					}
				}
				Skill s = Skill.FISHING;
				if (event.getCaught() instanceof Item) {
					ItemStack item = ((Item) event.getCaught()).getItemStack();
					Material mat = item.getType();
					if (XMaterial.isNewVersion()) {
						if (mat.equals(XMaterial.COD.parseMaterial())) {
							Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.RAW_FISH));
						} else if (mat.equals(XMaterial.SALMON.parseMaterial())) {
							Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.RAW_SALMON));
						} else if (mat.equals(XMaterial.TROPICAL_FISH.parseMaterial())) {
							Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.CLOWNFISH));
						} else if (mat.equals(XMaterial.PUFFERFISH.parseMaterial())) {
							Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.PUFFERFISH));
						}
					} else if (mat.equals(XMaterial.COD.parseMaterial())) {
						switch (item.getDurability()) {
							case 0:
								Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.RAW_FISH));
								break;
							case 1:
								Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.RAW_SALMON));
								break;
							case 2:
								Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.CLOWNFISH));
								break;
							case 3:
								Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.PUFFERFISH));
								break;
						}
					}
					if (mat.equals(Material.BOW) || mat.equals(Material.ENCHANTED_BOOK) || mat.equals(Material.NAME_TAG) || mat.equals(Material.SADDLE)) {
						Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.TREASURE));
					} else if (mat.equals(Material.BOWL) || mat.equals(Material.LEATHER) || mat.equals(Material.LEATHER_BOOTS) || mat.equals(Material.ROTTEN_FLESH)
							|| mat.equals(Material.POTION) || mat.equals(Material.BONE) || mat.equals(Material.TRIPWIRE_HOOK) || mat.equals(Material.STICK)
							|| mat.equals(Material.STRING) || mat.equals(XMaterial.INK_SAC.parseMaterial()) || mat.equals(XMaterial.LILY_PAD.parseMaterial())) {
						Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.JUNK));
					} else if (mat.equals(Material.FISHING_ROD)) {
						if (item.getEnchantments().size() != 0) {
							Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.TREASURE));
						} else {
							Leveler.addXp(p, s, FishingAbilities.getModifiedXp(p, Source.JUNK));
						}
					}
				}
			}
		}
	}
	
	
}
