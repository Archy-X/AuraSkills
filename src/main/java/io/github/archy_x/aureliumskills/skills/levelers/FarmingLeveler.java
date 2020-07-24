package io.github.archy_x.aureliumskills.skills.levelers;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.Source;
import io.github.archy_x.aureliumskills.skills.abilities.FarmingAbilities;
import io.github.archy_x.aureliumskills.util.XMaterial;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;

@SuppressWarnings("deprecation")
public class FarmingLeveler implements Listener{

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (Options.isEnabled(Skill.FARMING)) {
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
				Player p = event.getPlayer();
				Block b = event.getBlock();
				Skill s = Skill.FARMING;
				Material mat = b.getType();
				if (mat.equals(Material.CARROT)) {
					Crops crops = (Crops) b.getState().getData();
					if (crops.getState().equals(CropState.RIPE)) {
						Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.CARROT));
						applyAbilities(p, b);
					}
				}
				else if (mat.equals(Material.POTATO)) {
					Crops crops = (Crops) b.getState().getData();
					if (crops.getState().equals(CropState.RIPE)) {
						Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.POTATO));
						applyAbilities(p, b);
					}
				}
				else if (mat.equals(XMaterial.BEETROOT.parseMaterial())) {
					Crops crops = (Crops) b.getState().getData();
					if (crops.getState().equals(CropState.RIPE)) {
						Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.BEETROOT));
						applyAbilities(p, b);
					}
				}
				else if (mat.equals(XMaterial.NETHER_WART.parseMaterial())) {
					NetherWarts crops = (NetherWarts) b.getState().getData();
					if (crops.getState().equals(NetherWartsState.RIPE)) {
						Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.NETHER_WART));
						applyAbilities(p, b);
					}
				}
				else if (mat.equals(Material.PUMPKIN)) {
					if (!b.hasMetadata("skillsPlaced")) {
						Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.PUMPKIN));
						applyAbilities(p, b);
					}
				}
				else if (mat.equals(XMaterial.MELON.parseMaterial())) {
					if (!b.hasMetadata("skillsPlaced")) {
						Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.MELON));
						applyAbilities(p, b);
					}
				}
				else if (mat.name().equals("CROPS") || mat.equals(XMaterial.WHEAT.parseMaterial())) {
					Crops crops = (Crops) b.getState().getData();
					if (crops.getState().equals(CropState.RIPE)) {
						Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.WHEAT));
						applyAbilities(p, b);
					}
				}
			}
		}
	}
	
	private void applyAbilities(Player player, Block block) {
		FarmingAbilities.bountifulHarvest(player, block);
		FarmingAbilities.tripleHarvest(player, block);
	}
	
}
