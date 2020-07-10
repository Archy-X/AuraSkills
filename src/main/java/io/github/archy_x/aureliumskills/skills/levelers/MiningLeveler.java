package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.Source;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class MiningLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (Options.isEnabled(Skill.MINING)) {
			if (event.isCancelled() == false) {
				if (!event.getBlock().hasMetadata("skillsPlaced")) {
					Player p = event.getPlayer();
					Skill s = Skill.MINING;
					Material mat = event.getBlock().getType();
					if (mat.equals(Material.STONE)) {
						Leveler.addXp(p, s, Source.STONE);
					}
					else if (mat.equals(Material.COBBLESTONE)) {
						Leveler.addXp(p, s, Source.COBBLESTONE);
					}
					else if (mat.equals(Material.COAL_ORE)) {
						Leveler.addXp(p, s, Source.COAL_ORE);
					}
					else if (mat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial())) {
						Leveler.addXp(p, s, Source.QUARTZ_ORE);
					}
					else if (mat.equals(Material.IRON_ORE)) {
						Leveler.addXp(p, s, Source.IRON_ORE);
					}
					else if (mat.equals(XMaterial.REDSTONE_ORE.parseMaterial()) || mat.name().equals("GLOWING_REDSTONE_ORE")) {
						Leveler.addXp(p, s, Source.REDSTONE_ORE);
					}
					else if (mat.equals(Material.LAPIS_ORE)) {
						Leveler.addXp(p, s, Source.LAPIS_ORE);
					}
					else if (mat.equals(Material.GOLD_ORE)) {
						Leveler.addXp(p, s, Source.GOLD_ORE);
					}
					else if (mat.equals(Material.DIAMOND_ORE)) {
						Leveler.addXp(p, s, Source.DIAMOND_ORE);
					}
					else if (mat.equals(Material.EMERALD_ORE)) {
						Leveler.addXp(p, s, Source.EMERALD_ORE);
					}
				}
			}
		}
	}
}
