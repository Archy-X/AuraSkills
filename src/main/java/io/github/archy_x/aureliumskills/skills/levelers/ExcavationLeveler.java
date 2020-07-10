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

public class ExcavationLeveler implements Listener{

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (Options.isEnabled(Skill.EXCAVATION)) {
			if (event.isCancelled() == false) {
				if (!event.getBlock().hasMetadata("skillsPlaced")) {
					Skill s = Skill.EXCAVATION;
					Player p = event.getPlayer();
					Material mat = event.getBlock().getType();
					if (mat.equals(Material.DIRT)) {
						Leveler.addXp(p, s, Source.DIRT);
					}
					else if (mat.equals(Material.SAND)) {
						Leveler.addXp(p, s, Source.SAND);
					}
					else if (mat.equals(XMaterial.GRASS_BLOCK.parseMaterial())) {
						Leveler.addXp(p, s, Source.GRASS_BLOCK);
					}
					else if (mat.equals(Material.GRAVEL)) {
						Leveler.addXp(p, s, Source.GRAVEL);
					}
					else if (mat.equals(Material.CLAY)) {
						Leveler.addXp(p, s, Source.CLAY);
					}
					else if (mat.equals(Material.SOUL_SAND)) {
						Leveler.addXp(p, s, Source.SOUL_SAND);
					}
					else if (mat.equals(XMaterial.MYCELIUM.parseMaterial())) {
						Leveler.addXp(p, s, Source.MYCELIUM);
					}
				}
			}
		}
	}
}
