package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.ExcavationAbilities;
import com.archyx.aureliumskills.util.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ExcavationLeveler implements Listener{

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (Options.isEnabled(Skill.EXCAVATION)) {
			//Check cancelled
			if (Options.getCheckCancelled(Skill.EXCAVATION)) {
				if (event.isCancelled()) {
					return;
				}
			}
			//Checks if in blocked world
			if (AureliumSkills.worldManager.isInBlockedWorld(event.getBlock().getLocation())) {
				return;
			}
			//Checks if in blocked region
			if (AureliumSkills.worldGuardEnabled) {
				if (AureliumSkills.worldGuardSupport.isInBlockedRegion(event.getBlock().getLocation())) {
					return;
				}
			}
			//Check block replace
			if (Options.checkBlockReplace) {
				if (event.getBlock().hasMetadata("skillsPlaced")) {
					return;
				}
			}
			Skill s = Skill.EXCAVATION;
			Block b = event.getBlock();
			Player p = event.getPlayer();
			Material mat = event.getBlock().getType();
			//Check for permission
			if (!p.hasPermission("aureliumskills.excavation")) {
				return;
			}
			//Check creative mode disable
			if (Options.disableInCreativeMode) {
				if (p.getGameMode().equals(GameMode.CREATIVE)) {
					return;
				}
			}
			if (mat.equals(Material.SAND)) {
				if (XMaterial.isNewVersion()) {
					Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.SAND));
				}
				else {
					switch (b.getData()) {
						case 0:
							Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.SAND));
							break;
						case 1:
							Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.RED_SAND));
							break;
					}
				}
			}
			else if (mat.equals(XMaterial.RED_SAND.parseMaterial())) {
				Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.RED_SAND));
			}
			else if (mat.equals(XMaterial.GRASS_BLOCK.parseMaterial())) {
				Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.GRASS_BLOCK));
			}
			else if (mat.equals(Material.GRAVEL)) {
				Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.GRAVEL));
			}
			else if (mat.equals(Material.CLAY)) {
				Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.CLAY));
			}
			else if (mat.equals(Material.SOUL_SAND)) {
				Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.SOUL_SAND));
			}
			else if (mat.equals(XMaterial.MYCELIUM.parseMaterial())) {
				Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.MYCELIUM));
			}
			else if (mat.equals(XMaterial.SOUL_SOIL.parseMaterial())) {
				Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.SOUL_SOIL));
			}
			if (XMaterial.isNewVersion()) {
				if (mat.equals(Material.DIRT)) {
					Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.DIRT));
				}
				else if (mat.equals(XMaterial.COARSE_DIRT.parseMaterial())) {
					Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.COARSE_DIRT));
				}
				else if (mat.equals(XMaterial.PODZOL.parseMaterial())) {
					Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.PODZOL));
				}
			}
			else {
				if (mat.equals(Material.DIRT)) {
					switch (b.getData()) {
						case 0:
							Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.DIRT));
							break;
						case 1:
							Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.COARSE_DIRT));
							break;
						case 2:
							Leveler.addXp(p, s, ExcavationAbilities.getModifiedXp(p, Source.PODZOL));
							break;
					}
				}
			}
		}
	}
}
