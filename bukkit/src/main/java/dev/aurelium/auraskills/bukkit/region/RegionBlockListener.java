package dev.aurelium.auraskills.bukkit.region;

import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardHook;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.bukkit.util.BlockFaceUtil;
import dev.aurelium.auraskills.common.config.Option;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class RegionBlockListener implements Listener {

    private final AuraSkills plugin;
    private final BukkitRegionManager regionManager;
    private final BlockLeveler blockLeveler;

    public RegionBlockListener(AuraSkills plugin) {
        this.plugin = plugin;
        this.regionManager = plugin.getRegionManager();
        this.blockLeveler = plugin.getLevelManager().getLeveler(BlockLeveler.class);
    }

    @EventHandler
    public void checkPlace(BlockPlaceEvent event) {
        // Checks if world is blocked
        if (plugin.getWorldManager().isCheckReplaceDisabled(event.getBlock().getLocation())) {
            return;
        }
        // Checks if region is blocked
        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            if (plugin.getHookManager().getHook(WorldGuardHook.class).isInBlockedCheckRegion(event.getBlock().getLocation())) {
                return;
            }
        }
        if (!plugin.configBoolean(Option.CHECK_BLOCK_REPLACE_ENABLED)) return;

        Block block = event.getBlock();
        regionManager.handleBlockPlace(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
        checkTallPlant(block, 0, mat -> mat == Material.SUGAR_CANE);
        checkTallPlant(block, 0, mat -> mat == Material.BAMBOO);
        checkTallPlant(block, 0, mat -> mat == Material.CACTUS);
        checkSupportBelow(block);
        checkSupportSide(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            regionManager.addPlacedBlock(block.getRelative(event.getDirection()));
        }
        regionManager.removePlacedBlock(event.getBlock().getRelative(event.getDirection()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        int growY = event.getLocation().getBlockY();
        for (BlockState state : event.getBlocks()) {
            // Only remove placed blocks at same y level as sapling
            if (state.getLocation().getY() != growY) continue;

            regionManager.removePlacedBlock(state.getBlock());
        }
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
        SkillSource<BlockXpSource> skillSource = blockLeveler.getSource(block, BlockXpSource.BlockTriggers.BREAK);
        BlockXpSource source = skillSource == null ? null : skillSource.source();
        if (source != null && source.requiresSupportBlock(BlockXpSource.SupportBlockType.BELOW) && regionManager.isPlacedBlock(above)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Remove if block was destroyed
                    if (blockLeveler.isDifferentSource(block, source, BlockXpSource.BlockTriggers.BREAK)) {
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
            SkillSource<BlockXpSource> skillSource = blockLeveler.getSource(block, BlockXpSource.BlockTriggers.BREAK);
            BlockXpSource source = skillSource == null ? null : skillSource.source();
            if (source != null && source.requiresSupportBlock(BlockXpSource.SupportBlockType.SIDE) && regionManager.isPlacedBlock(checkedBlock)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Remove if block was destroyed
                        if (blockLeveler.isDifferentSource(block, source, BlockXpSource.BlockTriggers.BREAK)) {
                            regionManager.removePlacedBlock(block);
                        }
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }
}
