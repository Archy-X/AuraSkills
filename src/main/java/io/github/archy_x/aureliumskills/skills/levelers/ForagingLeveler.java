package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class ForagingLeveler implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled() == false) {
			if (!event.getBlock().hasMetadata("skillsPlaced")) {
				Material mat = event.getBlock().getType();
				if (mat.equals(XMaterial.OAK_LOG.parseMaterial()) || mat.equals(XMaterial.BIRCH_LOG.parseMaterial())
						|| mat.equals(XMaterial.SPRUCE_LOG.parseMaterial()) || mat.equals(XMaterial.JUNGLE_LOG.parseMaterial())) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FORAGING, 4.5);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.FORAGING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.FORAGING, 4.5);
					}
				}
				else if (mat.equals(XMaterial.DARK_OAK_LOG.parseMaterial()) || mat.equals(XMaterial.ACACIA_LOG.parseMaterial())) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FORAGING, 4.5);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.FORAGING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.FORAGING, 4.5);
					}
				}
				else if (mat.equals(XMaterial.OAK_LEAVES.parseMaterial()) || mat.equals(XMaterial.BIRCH_LEAVES.parseMaterial())
						|| mat.equals(XMaterial.SPRUCE_LEAVES.parseMaterial()) || mat.equals(XMaterial.JUNGLE_LEAVES.parseMaterial())) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FORAGING, 0.4);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.FORAGING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.FORAGING, 0.4);
					}
				}
				else if (mat.equals(XMaterial.DARK_OAK_LEAVES.parseMaterial()) || mat.equals(XMaterial.ACACIA_LEAVES.parseMaterial())) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.FORAGING, 0.4);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.FORAGING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.FORAGING, 0.4);
					}
				}
			}
		}
	}
}
