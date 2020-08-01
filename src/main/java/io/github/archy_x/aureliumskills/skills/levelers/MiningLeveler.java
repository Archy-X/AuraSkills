package io.github.archy_x.aureliumskills.skills.levelers;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.Source;
import io.github.archy_x.aureliumskills.skills.abilities.MiningAbilities;
import io.github.archy_x.aureliumskills.util.XMaterial;
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
			if (event.isCancelled() == false) {
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
			}
		}
	}
	
	private void applyAbilities(Player p, Block b) {
		MiningAbilities.luckyMiner(p, b);
	}
}
