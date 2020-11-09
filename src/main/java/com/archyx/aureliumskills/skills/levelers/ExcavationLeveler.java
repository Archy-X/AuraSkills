package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.GameMode;
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
		if (OptionL.isEnabled(Skill.EXCAVATION)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.EXCAVATION_CHECK_CANCELLED)) {
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
			if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE)) {
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
			if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
				if (p.getGameMode().equals(GameMode.CREATIVE)) {
					return;
				}
			}
			if (mat.equals(Material.SAND)) {
				if (XMaterial.isNewVersion()) {
					Leveler.addXp(p, s, getXp(p, Source.SAND));
				}
				else {
					switch (b.getData()) {
						case 0:
							Leveler.addXp(p, s, getXp(p, Source.SAND));
							break;
						case 1:
							Leveler.addXp(p, s, getXp(p, Source.RED_SAND));
							break;
					}
				}
			}
			else if (mat.equals(XMaterial.RED_SAND.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.RED_SAND));
			}
			else if (mat.equals(XMaterial.GRASS_BLOCK.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.GRASS_BLOCK));
			}
			else if (mat.equals(Material.GRAVEL)) {
				Leveler.addXp(p, s, getXp(p, Source.GRAVEL));
			}
			else if (mat.equals(Material.CLAY)) {
				Leveler.addXp(p, s, getXp(p, Source.CLAY));
			}
			else if (mat.equals(Material.SOUL_SAND)) {
				Leveler.addXp(p, s, getXp(p, Source.SOUL_SAND));
			}
			else if (mat.equals(XMaterial.MYCELIUM.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.MYCELIUM));
			}
			else if (mat.equals(XMaterial.SOUL_SOIL.parseMaterial())) {
				Leveler.addXp(p, s, getXp(p, Source.SOUL_SOIL));
			}
			if (XMaterial.isNewVersion()) {
				if (mat.equals(Material.DIRT)) {
					Leveler.addXp(p, s, getXp(p, Source.DIRT));
				}
				else if (mat.equals(XMaterial.COARSE_DIRT.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.COARSE_DIRT));
				}
				else if (mat.equals(XMaterial.PODZOL.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.PODZOL));
				}
			}
			else {
				if (mat.equals(Material.DIRT)) {
					switch (b.getData()) {
						case 0:
							Leveler.addXp(p, s, getXp(p, Source.DIRT));
							break;
						case 1:
							Leveler.addXp(p, s, getXp(p, Source.COARSE_DIRT));
							break;
						case 2:
							Leveler.addXp(p, s, getXp(p, Source.PODZOL));
							break;
					}
				}
			}
			// Check custom blocks
			checkCustomBlocks(p, b, s);
		}
	}
}
