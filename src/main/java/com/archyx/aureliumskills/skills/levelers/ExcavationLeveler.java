package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.sources.ExcavationSource;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ExcavationLeveler extends SkillLeveler implements Listener{

	public ExcavationLeveler(AureliumSkills plugin) {
		super(plugin, Ability.EXCAVATOR);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	@SuppressWarnings("deprecation")
	public void onBlockBreak(BlockBreakEvent event) {
		if (OptionL.isEnabled(Skills.EXCAVATION)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.EXCAVATION_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			Skill s = Skills.EXCAVATION;
			Block b = event.getBlock();
			//Check block replace
			if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE)) {
				if (plugin.getRegionManager().isPlacedBlock(b)) {
					return;
				}
			}
			Player p = event.getPlayer();
			if (blockXpGainLocation(event.getBlock().getLocation(), p)) return;
			Material mat = event.getBlock().getType();
			if (blockXpGainPlayer(p)) return;
			Leveler leveler = plugin.getLeveler();
			if (mat.equals(Material.SAND)) {
				if (XMaterial.isNewVersion()) {
					leveler.addXp(p, s, getXp(p, ExcavationSource.SAND));
				}
				else {
					switch (b.getData()) {
						case 0:
							leveler.addXp(p, s, getXp(p, ExcavationSource.SAND));
							break;
						case 1:
							leveler.addXp(p, s, getXp(p, ExcavationSource.RED_SAND));
							break;
					}
				}
			}
			else if (mat.equals(XMaterial.RED_SAND.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, ExcavationSource.RED_SAND));
			}
			else if (mat.equals(XMaterial.GRASS_BLOCK.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, ExcavationSource.GRASS_BLOCK));
			}
			else if (mat.equals(Material.GRAVEL)) {
				leveler.addXp(p, s, getXp(p, ExcavationSource.GRAVEL));
			}
			else if (mat.equals(Material.CLAY)) {
				leveler.addXp(p, s, getXp(p, ExcavationSource.CLAY));
			}
			else if (mat.equals(Material.SOUL_SAND)) {
				leveler.addXp(p, s, getXp(p, ExcavationSource.SOUL_SAND));
			}
			else if (mat.equals(XMaterial.MYCELIUM.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, ExcavationSource.MYCELIUM));
			}
			else if (mat.equals(XMaterial.SOUL_SOIL.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, ExcavationSource.SOUL_SOIL));
			}
			// TODO Add rooted dirt
			if (XMaterial.isNewVersion()) {
				if (mat.equals(Material.DIRT)) {
					leveler.addXp(p, s, getXp(p, ExcavationSource.DIRT));
				}
				else if (mat.equals(XMaterial.COARSE_DIRT.parseMaterial())) {
					leveler.addXp(p, s, getXp(p, ExcavationSource.COARSE_DIRT));
				}
				else if (mat.equals(XMaterial.PODZOL.parseMaterial())) {
					leveler.addXp(p, s, getXp(p, ExcavationSource.PODZOL));
				}
			}
			else {
				if (mat.equals(Material.DIRT)) {
					switch (b.getData()) {
						case 0:
							leveler.addXp(p, s, getXp(p, ExcavationSource.DIRT));
							break;
						case 1:
							leveler.addXp(p, s, getXp(p, ExcavationSource.COARSE_DIRT));
							break;
						case 2:
							leveler.addXp(p, s, getXp(p, ExcavationSource.PODZOL));
							break;
					}
				}
			}
			// Check custom blocks
			checkCustomBlocks(p, b, s);
		}
	}
}
