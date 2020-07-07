package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class ExcavationLeveler implements Listener{

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled() == false) {
			if (!event.getBlock().hasMetadata("skillsPlaced")) {
				Material mat = event.getBlock().getType();
				if (mat.equals(Material.DIRT)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.EXCAVATION, 0.3);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.EXCAVATION);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.EXCAVATION, 0.3);
					}
				}
				else if (mat.equals(Material.SAND)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.EXCAVATION, 0.4);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.EXCAVATION);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.EXCAVATION, 0.4);
					}
				}
				else if (mat.equals(XMaterial.GRASS_BLOCK.parseMaterial())) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.EXCAVATION, 0.7);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.EXCAVATION);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.EXCAVATION, 0.7);
					}
				}
				else if (mat.equals(Material.GRAVEL)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.EXCAVATION, 1.5);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.EXCAVATION);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.EXCAVATION, 1.5);
					}
				}
				else if (mat.equals(Material.CLAY)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.EXCAVATION, 2.4);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.EXCAVATION);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.EXCAVATION, 2.4);
					}
				}
				else if (mat.equals(Material.SOUL_SAND)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.EXCAVATION, 2.7);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.EXCAVATION);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.EXCAVATION, 2.7);
					}
				}
				else if (mat.equals(XMaterial.MYCELIUM.parseMaterial())) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.EXCAVATION, 3.7);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.EXCAVATION);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.EXCAVATION, 3.7);
					}
				}
			}
		}
	}
}
