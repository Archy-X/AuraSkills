package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.Source;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class FishingLeveler implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFish(PlayerFishEvent event) {
		if (Options.isEnabled(Skill.FISHING)) {
			if (event.isCancelled() == false) {
				if (event.getState().equals(State.CAUGHT_FISH)) {
					Player p = event.getPlayer();
					Skill s = Skill.FISHING;
					ItemStack item = ((Item) event.getCaught()).getItemStack();
					Material mat = item.getType();
					if (XMaterial.isNewVersion()) {
						if (mat.equals(XMaterial.COD.parseMaterial())) {
							Leveler.addXp(p, s, Source.RAW_FISH);
						}
						else if (mat.equals(XMaterial.SALMON.parseMaterial())) {
							Leveler.addXp(p, s, Source.RAW_SALMON);
						}
						else if (mat.equals(XMaterial.TROPICAL_FISH.parseMaterial())) {
							Leveler.addXp(p, s, Source.CLOWNFISH);
						}
						else if (mat.equals(XMaterial.PUFFERFISH.parseMaterial())) {
							Leveler.addXp(p, s, Source.PUFFERFISH);
						}
					}
					else if (mat.equals(XMaterial.COD.parseMaterial())) {
						switch(item.getDurability()) {
							case 0:
								Leveler.addXp(p, s, Source.RAW_FISH);
							case 1:
								Leveler.addXp(p, s, Source.RAW_SALMON);
							case 2:
								Leveler.addXp(p, s, Source.CLOWNFISH);
							case 3:
								Leveler.addXp(p, s, Source.PUFFERFISH);
						}
					}
					if (mat.equals(Material.BOW) || mat.equals(Material.ENCHANTED_BOOK) || mat.equals(Material.NAME_TAG) || mat.equals(Material.SADDLE)) {
						Leveler.addXp(p, s, Source.TREASURE);
					}
					else if (mat.equals(Material.BOWL) || mat.equals(Material.LEATHER) || mat.equals(Material.LEATHER_BOOTS) || mat.equals(Material.ROTTEN_FLESH)
							|| mat.equals(Material.POTION) || mat.equals(Material.BONE) || mat.equals(Material.TRIPWIRE_HOOK) || mat.equals(Material.STICK) 
							|| mat.equals(Material.STRING) || mat.equals(XMaterial.INK_SAC.parseMaterial())) {
						Leveler.addXp(p, s, Source.JUNK);
					}
					else if (mat.equals(Material.FISHING_ROD)) {
						if (item.getEnchantments().size() != 0) {
							Leveler.addXp(p, s, Source.TREASURE);
						}
						else {
							Leveler.addXp(p, s, Source.JUNK);
						}
					}
				}
			}
		}
	}
	
	
}
