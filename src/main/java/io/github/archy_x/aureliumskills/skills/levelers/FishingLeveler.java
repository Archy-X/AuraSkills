package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class FishingLeveler implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFish(PlayerFishEvent event) {
		if (event.isCancelled() == false) {
			if (event.getState().equals(State.CAUGHT_FISH)) {
				ItemStack item = ((Item) event.getCaught()).getItemStack();
				Material mat = item.getType();
				if (mat.equals(XMaterial.COD.parseMaterial())) {
					if (item.getDurability() == 0) {
						if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
							SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FISHING, 25);
							Leveler.playSound(event.getPlayer());
							Leveler.checkLevelUp(event.getPlayer(), Skill.FISHING);
							Leveler.sendActionBarMessage(event.getPlayer(), Skill.FISHING, 25);
						}
					}
					else if (item.getDurability() == 1) {
						if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
							SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FISHING, 60);
							Leveler.playSound(event.getPlayer());
							Leveler.checkLevelUp(event.getPlayer(), Skill.FISHING);
							Leveler.sendActionBarMessage(event.getPlayer(), Skill.FISHING, 60);
						}
					}
					else if (item.getDurability() == 2) {
						if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
							SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FISHING, 750);
							Leveler.playSound(event.getPlayer());
							Leveler.checkLevelUp(event.getPlayer(), Skill.FISHING);
							Leveler.sendActionBarMessage(event.getPlayer(), Skill.FISHING, 750);
						}
					}
					else if (item.getDurability() == 3) {
						if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
							SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FISHING, 115);
							Leveler.playSound(event.getPlayer());
							Leveler.checkLevelUp(event.getPlayer(), Skill.FISHING);
							Leveler.sendActionBarMessage(event.getPlayer(), Skill.FISHING, 115);
						}
					}
				}
				else if (mat.equals(Material.BOW) || mat.equals(Material.ENCHANTED_BOOK) || mat.equals(Material.NAME_TAG) || mat.equals(Material.SADDLE)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FISHING, 1000);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.FISHING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.FISHING, 1000);
					}
				}
				else if (mat.equals(Material.BOWL) || mat.equals(Material.LEATHER) || mat.equals(Material.LEATHER_BOOTS) || mat.equals(Material.ROTTEN_FLESH)
						|| mat.equals(Material.POTION) || mat.equals(Material.BONE) || mat.equals(Material.TRIPWIRE_HOOK)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FISHING, 15);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.FISHING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.FISHING, 15);
					}
				}
				else if (mat.equals(Material.FISHING_ROD)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FISHING, 180);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.FISHING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.FISHING, 180);
					}
				}
				else if (mat.equals(Material.STICK) || mat.equals(Material.STRING)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FISHING, 30);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.FISHING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.FISHING, 30);
					}
				}
				else if (mat.equals(XMaterial.INK_SAC.parseMaterial())) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FISHING, 180);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.FISHING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.FISHING, 180);
					}
				}
			}
		}
	}
	
}
