package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class MiningLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled() == false) {
			if (!event.getBlock().hasMetadata("skillsPlaced")) {
				Material mat = event.getBlock().getType();
				if (mat.equals(Material.STONE)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.MINING, 0.2);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.MINING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.MINING, 0.2);
					}
				}
				else if (mat.equals(Material.COBBLESTONE)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.MINING, 0.2);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.MINING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.MINING, 0.2);
					}
				}
				else if (mat.equals(Material.COAL_ORE)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.MINING, 1);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.MINING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.MINING, 1);
					}
				}
				else if (mat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial())) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.MINING, 1.8);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.MINING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.MINING, 1.8);
					}
				}
				else if (mat.equals(Material.IRON_ORE)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.MINING, 1.8);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.MINING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.MINING, 1.8);
					}
				}
				else if (mat.equals(XMaterial.REDSTONE_ORE.parseMaterial())) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.MINING, 5.7);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.MINING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.MINING, 5.7);
					}
				}
				else if (mat.equals(Material.LAPIS_ORE)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.MINING, 40.6);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.MINING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.MINING, 40.6);
					}
				}
				else if (mat.equals(Material.GOLD_ORE)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.MINING, 17.8);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.MINING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.MINING, 17.8);
					}
				}
				else if (mat.equals(Material.DIAMOND_ORE)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.MINING, 47.3);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.MINING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.MINING, 47.3);
					}
				}
				else if (mat.equals(Material.EMERALD_ORE)) {
					if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
						SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).addXp(Skill.MINING, 142);
						Leveler.playSound(event.getPlayer());
						Leveler.checkLevelUp(event.getPlayer(), Skill.MINING);
						Leveler.sendActionBarMessage(event.getPlayer(), Skill.MINING, 142);
					}
				}
			}
		}
	}
}
