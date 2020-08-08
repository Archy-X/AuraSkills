package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.abilities.MiningAbilities;
import com.archyx.aureliumskills.util.XMaterial;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MiningLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (Options.isEnabled(Skill.MINING)) {
			//Check cancelled
			if (Options.getCheckCancelled(Skill.MINING)) {
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
			Player p = event.getPlayer();
			Block b = event.getBlock();
			Skill s = Skill.MINING;
			Material mat = event.getBlock().getType();
			if (mat.equals(Material.STONE)) {
				Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.STONE));
			}
			else if (mat.equals(Material.COBBLESTONE)) {
				Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.COBBLESTONE));
			}
			else if (mat.equals(Material.COAL_ORE)) {
				Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.COAL_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial())) {
				Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.QUARTZ_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.IRON_ORE)) {
				Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.IRON_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.REDSTONE_ORE.parseMaterial()) || mat.name().equals("GLOWING_REDSTONE_ORE")) {
				Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.REDSTONE_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.LAPIS_ORE)) {
				Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.LAPIS_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.GOLD_ORE)) {
				Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.GOLD_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.DIAMOND_ORE)) {
				Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.DIAMOND_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.EMERALD_ORE)) {
				Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.EMERALD_ORE));
				applyAbilities(p, b);
			}
			else if (XMaterial.isNewVersion()) {
				if (mat.equals(XMaterial.TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.TERRACOTTA));
					applyAbilities(p, b);
				}
				else if (mat.equals(XMaterial.RED_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.RED_TERRACOTTA));
					applyAbilities(p, b);
				}
				else if (mat.equals(XMaterial.ORANGE_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.ORANGE_TERRACOTTA));
					applyAbilities(p, b);
				}
				else if (mat.equals(XMaterial.YELLOW_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.YELLOW_TERRACOTTA));
					applyAbilities(p, b);
				}
				else if (mat.equals(XMaterial.WHITE_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.WHITE_TERRACOTTA));
					applyAbilities(p, b);
				}
				else if (mat.equals(XMaterial.LIGHT_GRAY_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.LIGHT_GRAY_TERRACOTTA));
					applyAbilities(p, b);
				}
				else if (mat.equals(XMaterial.BROWN_TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.BROWN_TERRACOTTA));
					applyAbilities(p, b);
				}
			}
			else {
				if (mat.equals(XMaterial.TERRACOTTA.parseMaterial())) {
					Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.TERRACOTTA));
					applyAbilities(p, b);
				}
				else if (mat.equals(XMaterial.WHITE_TERRACOTTA.parseMaterial())) {
					switch (b.getData()) {
						case 0:
							Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.WHITE_TERRACOTTA));
							applyAbilities(p, b);
							break;
						case 1:
							Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.ORANGE_TERRACOTTA));
							applyAbilities(p, b);
							break;
						case 4:
							Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.YELLOW_TERRACOTTA));
							applyAbilities(p, b);
							break;
						case 8:
							Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.LIGHT_GRAY_TERRACOTTA));
							applyAbilities(p, b);
							break;
						case 12:
							Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.BROWN_TERRACOTTA));
							applyAbilities(p, b);
							break;
						case 14:
							Leveler.addXp(p, s, MiningAbilities.getModifiedXp(p, Source.RED_TERRACOTTA));
							applyAbilities(p, b);
							break;
					}
				}
			}
		}
	}
	
	private void applyAbilities(Player p, Block b) {
		MiningAbilities.luckyMiner(p, b);
	}
}
