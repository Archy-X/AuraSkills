package com.archyx.aureliumskills.skills.farming;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.leveler.SkillLeveler;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.SourceTag;
import com.archyx.aureliumskills.util.block.BlockUtil;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.CaveVinesPlant;
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
		if (!OptionL.isEnabled(Skills.FARMING)) return;
		// Check cancelled
		if (OptionL.getBoolean(Option.FARMING_CHECK_CANCELLED) && event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		if (blockXpGainLocation(event.getBlock().getLocation(), player)) return;
		Block block = event.getBlock();
		if (blockXpGainPlayer(player)) return;

		for (FarmingSource source : FarmingSource.values()) {
			if (!source.isMatch(block)) continue;
			// Check block replace
			if (source.shouldCheckBlockReplace() && OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(block)) {
				return;
			}

			double multiplier = 1.0;
			// Handle sugar cane
			if (source == FarmingSource.SUGAR_CANE) {
				int numBroken = 1;
				BlockState above = block.getRelative(BlockFace.UP).getState();
				if (XBlock.isSugarCane(above.getType())) {
					if (!plugin.getRegionManager().isPlacedBlock(above.getBlock())) {
						numBroken++;
					}
					BlockState aboveAbove = block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getState();
					if (XBlock.isSugarCane(aboveAbove.getType())) {
						if (!plugin.getRegionManager().isPlacedBlock(aboveAbove.getBlock())) {
							numBroken++;
						}
					}
				}
				multiplier = numBroken;
			}
			// Handle blocks that grow upwards
			if (source == FarmingSource.BAMBOO || source == FarmingSource.CACTUS || source == FarmingSource.KELP) {
				int numBroken = 1;
				if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(block)) {
					if (!source.isMatch(block.getRelative(BlockFace.UP)) || plugin.getRegionManager().isPlacedBlock(block.getRelative(BlockFace.UP))) {
						return;
					}
					numBroken = 0;
				}
				numBroken += getSameBlocksAbove(block.getRelative(BlockFace.UP), source, 0);
				multiplier = numBroken;
			}
			// Handle sea pickles
			if (source == FarmingSource.SEA_PICKLE) {
				if (block.getBlockData() instanceof SeaPickle) {
					SeaPickle seaPickle = (SeaPickle) block.getBlockData();
					multiplier = seaPickle.getPickles();
				}
			}
			// Handle sweet berry bush
			if (source == FarmingSource.SWEET_BERRY_BUSH) {
				multiplier = BlockUtil.getGrowthStage(block) - 1;
			}
			// Handle glow berries
			if (source == FarmingSource.GLOW_BERRIES) {
				if (!(block.getBlockData() instanceof CaveVinesPlant)) return;
				CaveVinesPlant caveVinesPlant = (CaveVinesPlant) block.getBlockData();
				if (!caveVinesPlant.isBerries()) return; // Only give xp if has berries
			}
			// Give XP
			giveXp(player, getXp(player, source) * multiplier, source, block);
			break;
		}
		// Check custom blocks
		checkCustomBlocks(player, block, Skills.FARMING);
	}

	private void giveXp(Player player, double amount, FarmingSource source, Block block) {
		// Give XP
		plugin.getLeveler().addXp(player, Skills.FARMING, amount);
		// Handle abilities
		if (hasTag(source, SourceTag.BOUNTIFUL_HARVEST_APPLICABLE)) {
			farmingAbilities.bountifulHarvest(player, block);
		}
		if (hasTag(source, SourceTag.TRIPLE_HARVEST_APPLICABLE)) {
			farmingAbilities.tripleHarvest(player, block);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onRightClick(PlayerInteractEvent event) {
		if (!OptionL.isEnabled(Skills.FARMING)) return;
		if (OptionL.getBoolean(Option.FARMING_CHECK_CANCELLED) && event.useInteractedBlock() == Event.Result.DENY) {
			return;
		}

		Player player = event.getPlayer();
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if (block == null) return;

		if (blockXpGainLocation(block.getLocation(), player)) return;
		if (blockXpGainPlayer(player)) return;

		for (FarmingSource source : FarmingSource.values()) {
			if (!source.isRightClickHarvestable()) continue;

			if (!source.isMatch(block)) continue;
			if (player.isSneaking() && isHoldingItem(player)) {
				return;
			}

			// Handle sweet berry bush
			if (source == FarmingSource.SWEET_BERRY_BUSH) {
				final double multiplier = BlockUtil.getGrowthStage(block) - 1;
				new BukkitRunnable() {
					@Override
					public void run() {
						if (block.getType() == XMaterial.SWEET_BERRY_BUSH.parseMaterial()) {
							if (BlockUtil.getGrowthStage(block) <= 1) {
								giveXp(player, getXp(player, source) * multiplier, source, block);
							}
						}
					}
				}.runTaskLater(plugin, 1L);
			}
			// Handle glow berries
			if (source == FarmingSource.GLOW_BERRIES) {
				if (!(block.getBlockData() instanceof CaveVinesPlant)) return;
				CaveVinesPlant caveVinesPlant = (CaveVinesPlant) block.getBlockData();
				if (caveVinesPlant.isBerries()) {
					giveXp(player, getXp(player, source), source, block);
				}
			}
			break;
		}
	}

	private boolean isHoldingItem(Player player) {
		return player.getInventory().getItemInMainHand().getType() != Material.AIR || player.getInventory().getItemInOffHand().getType() != Material.AIR;
	}

	private int getSameBlocksAbove(Block block, FarmingSource source, int num) {
		if (source.isMatch(block)) {
			if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(block)) {
				return num;
			}
			num++;
			return getSameBlocksAbove(block.getRelative(BlockFace.UP), source, num);
		}
		return num;
	}

}
