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

public class ForagingLeveler implements Listener{
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (Options.isEnabled(Skill.FORAGING)) {
			if (event.isCancelled() == false) {
				if (!event.getBlock().hasMetadata("skillsPlaced")) {
					Player p = event.getPlayer();
					Skill s = Skill.FORAGING;
					Material mat = event.getBlock().getType();
					//If 1.13+
					if (XMaterial.isNewVersion()) {
						if (mat.equals(XMaterial.OAK_LOG.parseMaterial())) {
							Leveler.addXp(p, s, Source.OAK_LOG);
						}
						else if (mat.equals(XMaterial.SPRUCE_LOG.parseMaterial())) {
							Leveler.addXp(p, s, Source.SPRUCE_LOG);
						}
						else if (mat.equals(XMaterial.BIRCH_LOG.parseMaterial())) {
							Leveler.addXp(p, s, Source.BIRCH_LOG);
						}
						else if (mat.equals(XMaterial.ACACIA_LOG.parseMaterial())) {
							Leveler.addXp(p, s, Source.ACACIA_LOG);
						}
						else if (mat.equals(XMaterial.DARK_OAK_LOG.parseMaterial())) {
							Leveler.addXp(p, s, Source.DARK_OAK_LOG);
						}
						else if (mat.equals(XMaterial.OAK_LEAVES.parseMaterial())) {
							Leveler.addXp(p, s, Source.OAK_LEAVES);
						}
						else if (mat.equals(XMaterial.SPRUCE_LEAVES.parseMaterial())) {
							Leveler.addXp(p, s, Source.SPRUCE_LEAVES);
						}
						else if (mat.equals(XMaterial.BIRCH_LEAVES.parseMaterial())) {
							Leveler.addXp(p, s, Source.BIRCH_LEAVES);
						}
						else if (mat.equals(XMaterial.ACACIA_LEAVES.parseMaterial())) {
							Leveler.addXp(p, s, Source.ACACIA_LEAVES);
						}
						else if (mat.equals(XMaterial.DARK_OAK_LEAVES.parseMaterial())) {
							Leveler.addXp(p, s, Source.DARK_OAK_LEAVES);
						}
					}
					//If legacy version (1.12)
					else {
						//If legacy material LOG
						if (mat.equals(XMaterial.OAK_LOG.parseMaterial())) {
							switch(event.getBlock().getData()) {
								case 0:
									Leveler.addXp(p, s, Source.OAK_LOG);
									break;
								case 1:
									Leveler.addXp(p, s, Source.SPRUCE_LOG);
									break;
								case 2:
									Leveler.addXp(p, s, Source.BIRCH_LOG);
									break;
								case 3:
									Leveler.addXp(p, s, Source.JUNGLE_LOG);
									break;
							}
						}
						//If legacy material LOG_2
						else if (mat.equals(XMaterial.ACACIA_LOG.parseMaterial())) {
							switch(event.getBlock().getData()) {
								case 0:
									Leveler.addXp(p, s, Source.ACACIA_LOG);
									break;
								case 1:
									Leveler.addXp(p, s, Source.DARK_OAK_LOG);
									break;
							}
						}
						//If legacy material LEAVES
						else if (mat.equals(XMaterial.OAK_LEAVES.parseMaterial())) {
							byte data = event.getBlock().getData();
							if (data == 0 || data == 8) {
								Leveler.addXp(p, s, Source.OAK_LEAVES);
							}
							else if (data == 1 || data == 9) {
								Leveler.addXp(p, s, Source.SPRUCE_LEAVES);
							}
							else if (data == 2 || data == 10) {
								Leveler.addXp(p, s, Source.BIRCH_LEAVES);
							}	
							else if (data == 3 || data == 11) {
								Leveler.addXp(p, s, Source.JUNGLE_LEAVES);
							}	
						}
						//If legacy material LEAVES_2
						else if (mat.equals(XMaterial.ACACIA_LEAVES.parseMaterial())) {
							byte data = event.getBlock().getData();
							if (data == 0 || data == 8) {
								Leveler.addXp(p, s, Source.ACACIA_LEAVES);
							}
							else if (data == 1 || data == 9) {
								Leveler.addXp(p, s, Source.DARK_OAK_LEAVES);
							}
						}
					}
				}
			}
		}
	}
}
