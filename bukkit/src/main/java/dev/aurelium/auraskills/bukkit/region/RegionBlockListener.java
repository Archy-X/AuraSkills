package dev.aurelium.auraskills.bukkit.region;

import com.archyx.aureliumskills.util.block.BlockFaceUtil;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardHook;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class RegionBlockListener implements Listener {

    private final AuraSkills plugin;
    private final RegionManager regionManager;
    private final BlockLeveler blockLeveler;

    public RegionBlockListener(AuraSkills plugin) {
        this.plugin = plugin;
        this.regionManager = plugin.getRegionManager();
        this.blockLeveler = plugin.getLevelManager().getLeveler(BlockLeveler.class);
    }

    @EventHandler
    public void checkPlace(BlockPlaceEvent event) {
        // Checks if world is blocked
        if (plugin.getWorldManager().isInBlockedCheckWorld(event.getBlock().getLocation())) {
            return;
        }
        // Checks if region is blocked
        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            if (plugin.getHookManager().getHook(WorldGuardHook.class).isInBlockedCheckRegion(event.getBlock().getLocation())) {
                return;
            }
        }
        if (!plugin.configBoolean(Option.CHECK_BLOCK_REPLACE)) return;

        Block block = event.getBlock();

        Pair<BlockXpSource, Skill> sourcePair = blockLeveler.getSource(block, BlockXpSource.BlockTriggers.BREAK);

        if (sourcePair == null) { // Not a source
            return;
        }

        BlockXpSource source = sourcePair.first();

        if (!source.checkReplace()) { // Check source option
            return;
        }

        regionManager.addPlacedBlock(block);
    }

    @EventHandler
    public void onSandFall(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        if (!regionManager.isPlacedBlock(block)) return;
        Material type = block.getType();
        if (type == Material.SAND || type == Material.RED_SAND || type == Material.GRAVEL) {
            Block below = block.getRelative(BlockFace.DOWN);
            if (below.getType() == Material.AIR || below.getType() == Material.CAVE_AIR || below.getType() == Material.VOID_AIR
                    || below.getType() == Material.WATER || below.getType() == Material.BUBBLE_COLUMN || below.getType() == Material.LAVA) {
                regionManager.removePlacedBlock(block);
                Entity entity = event.getEntity();
                AtomicInteger counter = new AtomicInteger();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Block currentBlock = entity.getLocation().getBlock();
                        if (entity.isDead() || !entity.isValid()) {
                            if (currentBlock.getType() == type) {
                                regionManager.addPlacedBlock(entity.getLocation().getBlock());
                            }
                            cancel();
                        } else if (currentBlock.getType().toString().contains("WEB")) {
                            cancel();
                        } else if (counter.incrementAndGet() >= 200) {
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 1L, 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        regionManager.removePlacedBlock(block);
        checkTallPlant(block, 0, XBlock::isSugarCane);
        checkTallPlant(block, 0, mat -> mat == XMaterial.BAMBOO.parseMaterial());
        checkTallPlant(block, 0, mat -> mat == Material.CACTUS);
        checkSupportBelow(block);
        checkSupportSide(block);
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            regionManager.addPlacedBlock(block.getRelative(event.getDirection()));
        }
        regionManager.removePlacedBlock(event.getBlock().getRelative(event.getDirection()));
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        Block lastBlock = event.getBlock();
        for (Block block : event.getBlocks()) {
            if (regionManager.isPlacedBlock(block)) {
                regionManager.addPlacedBlock(block.getRelative(event.getDirection()));
                if (block.getLocation().distanceSquared(event.getBlock().getLocation()) > lastBlock.getLocation().distanceSquared(event.getBlock().getLocation())) {
                    lastBlock = block;
                }
            }
        }
        regionManager.removePlacedBlock(lastBlock);
    }

    private void checkTallPlant(Block block, int num, Predicate<Material> isMaterial) {
        if (num < 20) {
            Block above = block.getRelative(BlockFace.UP);
            if (isMaterial.test(above.getType())) {
                if (regionManager.isPlacedBlock(above)) {
                    regionManager.removePlacedBlock(above);
                    checkTallPlant(above, num + 1, isMaterial);
                }
            }
        }
    }

    private void checkSupportBelow(Block block) {
        // Check if the block above requires support
        Block above = block.getRelative(BlockFace.UP);
        Pair<BlockXpSource, Skill> sourcePair = blockLeveler.getSource(block, BlockXpSource.BlockTriggers.BREAK);
        BlockXpSource source = sourcePair == null ? null : sourcePair.first();
        if (source != null && source.requiresSupportBlock(BlockXpSource.SupportBlockType.BELOW) && regionManager.isPlacedBlock(above)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Remove if block was destroyed
                    if (!blockLeveler.matchesSource(block, source, BlockXpSource.BlockTriggers.BREAK)) {
                        regionManager.removePlacedBlock(above);
                    }
                }
            }.runTaskLater(plugin, 1);
        }
    }

    private void checkSupportSide(Block block) {
        // Check each side
        for (BlockFace face : BlockFaceUtil.getBlockSides()) {
            Block checkedBlock = block.getRelative(face);
            Pair<BlockXpSource, Skill> sourcePair = blockLeveler.getSource(block, BlockXpSource.BlockTriggers.BREAK);
            BlockXpSource source = sourcePair == null ? null : sourcePair.first();
            if (source != null && source.requiresSupportBlock(BlockXpSource.SupportBlockType.SIDE) && regionManager.isPlacedBlock(checkedBlock)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Remove if block was destroyed
                        if (!blockLeveler.matchesSource(block, source, BlockXpSource.BlockTriggers.BREAK)) {
                            regionManager.removePlacedBlock(block);
                        }
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }
}
