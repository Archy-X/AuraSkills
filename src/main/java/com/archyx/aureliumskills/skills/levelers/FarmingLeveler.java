package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.abilities.FarmingAbilities;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.util.block.BlockUtil;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class FarmingLeveler extends SkillLeveler implements Listener{

	private final FarmingAbilities farmingAbilities;

	public FarmingLeveler(AureliumSkills plugin) {
		super(plugin, Ability.FARMER);
		this.farmingAbilities = new FarmingAbilities(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (OptionL.isEnabled(Skills.FARMING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.FARMING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			Player p = event.getPlayer();
			if (blockXpGainLocation(event.getBlock().getLocation(), p)) return;
			Block b = event.getBlock();
			Skill s = Skills.FARMING;
			Material mat = b.getType();
			if (blockXpGainPlayer(p)) return;
			Leveler leveler = plugin.getLeveler();
			if (BlockUtil.isCarrot(mat) && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.CARROT));
				applyAbilities(p, b);
			} else if (BlockUtil.isPotato(mat) && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.POTATO));
				applyAbilities(p, b);
			} else if (BlockUtil.isBeetroot(mat) && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.BEETROOT));
				applyAbilities(p, b);
			} else if (BlockUtil.isNetherWart(mat) && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.NETHER_WART));
				applyAbilities(p, b);
			} else if (BlockUtil.isWheat(mat) && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.WHEAT));
				applyAbilities(p, b);
			} else if (mat == Material.COCOA && BlockUtil.isFullyGrown(b)) {
				leveler.addXp(p, s, getXp(p, Source.COCOA));
				applyAbilities(p, b);
			} else if (mat == Material.PUMPKIN) {
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) {
					return;
				}
				leveler.addXp(p, s, getXp(p, Source.PUMPKIN));
				applyAbilities(p, b);
			} else if (mat == XMaterial.MELON.parseMaterial()) {
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) {
					return;
				}
				leveler.addXp(p, s, getXp(p, Source.MELON));
				applyAbilities(p, b);
			} else if (XBlock.isSugarCane(mat)) {
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
			} else if (mat == XMaterial.BAMBOO.parseMaterial()) {
				int numBroken = 1;
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) {
					if (!b.getRelative(BlockFace.UP).getType().equals(XMaterial.BAMBOO.parseMaterial()) || plugin.getRegionManager().isPlacedBlock(b.getRelative(BlockFace.UP))) {
						return;
					}
					numBroken = 0;
				}
				numBroken += getSameBlocksAbove(b.getRelative(BlockFace.UP), XMaterial.BAMBOO, 0);
				leveler.addXp(p, s, getXp(p, Source.BAMBOO) * numBroken);
				applyAbilities(p, b);
			} else if (mat == Material.CACTUS) {
				int numBroken = 1;
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) {
					if (b.getRelative(BlockFace.UP).getType() != Material.CACTUS || plugin.getRegionManager().isPlacedBlock(b.getRelative(BlockFace.UP))) {
						return;
					}
					numBroken = 0;
				}
				numBroken += getSameBlocksAbove(b.getRelative(BlockFace.UP), XMaterial.CACTUS, 0);
				leveler.addXp(p, s, getXp(p, Source.CACTUS) * numBroken);
				applyAbilities(p, b);
			} else if (mat == Material.BROWN_MUSHROOM) {
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) return;
				leveler.addXp(p, s, getXp(p, Source.BROWN_MUSHROOM));
				applyAbilities(p, b);
			} else if (mat == Material.RED_MUSHROOM) {
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) return;
				leveler.addXp(p, s, getXp(p, Source.RED_MUSHROOM));
				applyAbilities(p, b);
			} else if (mat == XMaterial.KELP_PLANT.parseMaterial()) {
				int numBroken = 1;
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) {
					if (!b.getRelative(BlockFace.UP).getType().equals(XMaterial.KELP_PLANT.parseMaterial()) || plugin.getRegionManager().isPlacedBlock(b.getRelative(BlockFace.UP))) {
						return;
					}
					numBroken = 0;
				}
				numBroken += getSameBlocksAbove(b.getRelative(BlockFace.UP), XMaterial.KELP_PLANT, 0);
				leveler.addXp(p, s, getXp(p, Source.KELP) * numBroken);
				applyAbilities(p, b);
			} else if (mat == XMaterial.SEA_PICKLE.parseMaterial()) {
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(b)) return;
				if (b.getBlockData() instanceof SeaPickle) {
					SeaPickle seaPickle = (SeaPickle) b.getBlockData();
					leveler.addXp(p, s, getXp(p, Source.SEA_PICKLE) * seaPickle.getPickles()); // Multiply xp by number of pickles
					applyAbilities(p, b);
				}
			} else if (mat == XMaterial.SWEET_BERRY_BUSH.parseMaterial()) {
				if (BlockUtil.getGrowthStage(b) >= 2) {
					leveler.addXp(p, s, getXp(p, Source.SWEET_BERRY_BUSH) * (BlockUtil.getGrowthStage(b) - 1));
					applyAbilities(p, b);
				}
			}
			// TODO Add glow berries
			// Check custom blocks
			checkCustomBlocks(p, b, s);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onRightClick(PlayerInteractEvent event) {
		if (OptionL.getBoolean(Option.FARMING_CHECK_CANCELLED)) {
			if (event.useInteractedBlock() == Event.Result.DENY) return;
		}

		Player player = event.getPlayer();
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if (block == null) return;

		// Right clicking to harvest a sweet berry bush
		if (block.getType().equals(XMaterial.SWEET_BERRY_BUSH.parseMaterial())) {
			if (player.isSneaking()) return;
			if (BlockUtil.getGrowthStage(block) >= 2) {
				double xpAmount = getXp(player, Source.SWEET_BERRY_BUSH) * (BlockUtil.getGrowthStage(block) - 1);
				// Check that the berry bush was actually harvested
				new BukkitRunnable() {
					@Override
					public void run() {
						if (block.getType() == XMaterial.SWEET_BERRY_BUSH.parseMaterial()) {
							if (BlockUtil.getGrowthStage(block) <= 1) {
								plugin.getLeveler().addXp(player, Skills.FARMING, xpAmount);
								applyAbilities(player, block);
							}
						}
					}
				}.runTaskLater(plugin, 1L);
			}
		}
	}

	private void applyAbilities(Player player, Block block) {
		farmingAbilities.bountifulHarvest(player, block);
		farmingAbilities.tripleHarvest(player, block);
	}

	private int getSameBlocksAbove(Block block, XMaterial material, int num) {
		if (block.getState().getType().equals(material.parseMaterial())) {
			if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(block)) {
				return num;
			}
			num++;
			return getSameBlocksAbove(block.getRelative(BlockFace.UP), material, num);
		}
		return num;
	}

}
