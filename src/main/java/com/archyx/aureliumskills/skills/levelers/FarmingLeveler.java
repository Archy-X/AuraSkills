package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.abilities.FarmingAbilities;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.util.BlockUtil;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class FarmingLeveler extends SkillLeveler implements Listener{

	private final FarmingAbilities farmingAbilities;

	public FarmingLeveler(AureliumSkills plugin) {
		super(plugin, Ability.FARMER);
		this.farmingAbilities = new FarmingAbilities(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.FARMING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			if (blockXpGainLocation(event.getBlock().getLocation())) return;
			Player p = event.getPlayer();
			Block b = event.getBlock();
			Skill s = Skill.FARMING;
			Material mat = b.getType();
			if (blockXpGainPlayer(p)) return;
			Leveler leveler = plugin.getLeveler();
			if (BlockUtil.isCarrot(mat) && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.CARROT));
				applyAbilities(p, b);
			}
			else if (BlockUtil.isPotato(mat) && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.POTATO));
				applyAbilities(p, b);
			}
			else if (BlockUtil.isBeetroot(mat) && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.BEETROOT));
				applyAbilities(p, b);
			}
			else if (BlockUtil.isNetherWart(mat) && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.NETHER_WART));
				applyAbilities(p, b);
			}
			else if (BlockUtil.isWheat(mat) && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.WHEAT));
				applyAbilities(p, b);
			}
			else if (mat == Material.COCOA && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.COCOA));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.PUMPKIN)) {
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) {
					return;
				}
				leveler.addXp(p, s, getXp(p, Source.PUMPKIN));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.MELON.parseMaterial())) {
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) {
					return;
				}
				leveler.addXp(p, s, getXp(p, Source.MELON));
				applyAbilities(p, b);
			}
			else if (XBlock.isSugarCane(mat)) {
				int numBroken = 1;
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) {
					return;
				}
				BlockState above = b.getRelative(BlockFace.UP).getState();
				if (XBlock.isSugarCane(above.getType())) {
					if (!plugin.getRegionManager().isPlacedBlock(above.getBlock())) {
						numBroken++;
					}
					BlockState aboveAbove = b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getState();
					if (XBlock.isSugarCane(aboveAbove.getType())) {
						if (!plugin.getRegionManager().isPlacedBlock(aboveAbove.getBlock())) {
							numBroken++;
						}
					}
				}
				leveler.addXp(p, s, getXp(p, Source.SUGAR_CANE) * numBroken);
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.BAMBOO.parseMaterial())) {
				int numBroken = 1;
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) {
					if (!b.getRelative(BlockFace.UP).getType().equals(XMaterial.BAMBOO.parseMaterial()) || b.getRelative(BlockFace.UP).hasMetadata("skillsPlaced")) {
						return;
					}
					numBroken = 0;
				}
				numBroken += getBamboo(b.getRelative(BlockFace.UP), 0);
				leveler.addXp(p, s, getXp(p, Source.BAMBOO) * numBroken);
				applyAbilities(p, b);
			}
			// Check custom blocks
			checkCustomBlocks(p, b, s);
		}
	}

	private void applyAbilities(Player player, Block block) {
		farmingAbilities.bountifulHarvest(player, block);
		farmingAbilities.tripleHarvest(player, block);
	}

	private int getBamboo(Block block, int num) {
		if (block.getState().getType().equals(XMaterial.BAMBOO.parseMaterial())) {
			if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(block)) {
				return num;
			}
			num++;
			return getBamboo(block.getRelative(BlockFace.UP), num);
		}
		return num;
	}

}
