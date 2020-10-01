package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.FarmingAbilities;
import com.archyx.aureliumskills.util.BlockUtil;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class FarmingLeveler implements Listener{

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.FARMING_CHECK_CANCELLED)) {
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
			Player p = event.getPlayer();
			Block b = event.getBlock();
			Skill s = Skill.FARMING;
			Material mat = b.getType();
			//Check for permission
			if (!p.hasPermission("aureliumskills.farming")) {
				return;
			}
			//Check creative mode disable
			if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
				if (p.getGameMode().equals(GameMode.CREATIVE)) {
					return;
				}
			}
			if (BlockUtil.isCarrot(mat) && BlockUtil.isFullyGrown(b)) {
				Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.CARROT));
				applyAbilities(p, b);
			}
			else if (BlockUtil.isPotato(mat) && BlockUtil.isFullyGrown(b)) {
				Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.POTATO));
				applyAbilities(p, b);
			}
			else if (BlockUtil.isBeetroot(mat) && BlockUtil.isFullyGrown(b)) {
				Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.BEETROOT));
				applyAbilities(p, b);
			}
			else if (BlockUtil.isNetherWart(mat) && BlockUtil.isFullyGrown(b)) {
				Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.NETHER_WART));
				applyAbilities(p, b);
			}
			else if (BlockUtil.isWheat(mat) && BlockUtil.isFullyGrown(b)) {
				Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.WHEAT));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.PUMPKIN)) {
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && b.hasMetadata("skillsPlaced")) {
					return;
				}
				Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.PUMPKIN));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.MELON.parseMaterial())) {
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && b.hasMetadata("skillsPlaced")) {
					return;
				}
				Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.MELON));
				applyAbilities(p, b);
			}
			else if (XBlock.isSugarCane(mat)) {
				int numBroken = 1;
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && b.hasMetadata("skillsPlaced")) {
					if (!XBlock.isSugarCane(b.getRelative(BlockFace.UP).getType()) || b.getRelative(BlockFace.UP).hasMetadata("skillsPlaced")) {
						return;
					}
					numBroken = 0;
				}
				if (XBlock.isSugarCane(b.getRelative(BlockFace.UP).getState().getType())) {
					numBroken++;
					if (XBlock.isSugarCane(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getState().getType())) {
						numBroken++;
					}
				}
				Leveler.addXp(p, s, FarmingAbilities.getModifiedXp(p, Source.SUGAR_CANE) * numBroken);
				applyAbilities(p, b);
			}
		}
	}
	
	private void applyAbilities(Player player, Block block) {
		FarmingAbilities.bountifulHarvest(player, block);
		FarmingAbilities.tripleHarvest(player, block);
	}


}
