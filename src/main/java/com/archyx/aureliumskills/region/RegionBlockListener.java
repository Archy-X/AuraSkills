package com.archyx.aureliumskills.region;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.sources.SourceManager;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
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

public class RegionBlockListener implements Listener {

    private final XMaterial[] checkedMaterials = new XMaterial[] {
            // Ultra Common
            XMaterial.COBBLESTONE,
            XMaterial.DIRT,
            XMaterial.NETHERRACK,
            XMaterial.STONE,
            // Common
            XMaterial.SAND,
            XMaterial.GRAVEL,
            XMaterial.GRASS_BLOCK,
            XMaterial.OAK_LOG,
            XMaterial.BIRCH_LOG,
            XMaterial.SPRUCE_LOG,
            XMaterial.GRANITE,
            XMaterial.DIORITE,
            XMaterial.ANDESITE,
            // Uncommon
            XMaterial.JUNGLE_LOG,
            XMaterial.DARK_OAK_LOG,
            XMaterial.ACACIA_LOG,
            XMaterial.CRIMSON_STEM,
            XMaterial.WARPED_STEM,
            XMaterial.TERRACOTTA,
            XMaterial.CLAY,
            XMaterial.SOUL_SAND,
            XMaterial.COAL_ORE,
            XMaterial.IRON_ORE,
            XMaterial.BLACKSTONE,
            XMaterial.NETHER_QUARTZ_ORE,
            XMaterial.END_STONE,
            XMaterial.BASALT,
            XMaterial.OBSIDIAN,
            XMaterial.MYCELIUM,
            XMaterial.OAK_WOOD,
            XMaterial.SPRUCE_WOOD,
            XMaterial.BIRCH_WOOD,
            XMaterial.JUNGLE_WOOD,
            XMaterial.ACACIA_WOOD,
            XMaterial.DARK_OAK_WOOD,
            XMaterial.CACTUS,
            XMaterial.BROWN_MUSHROOM,
            XMaterial.RED_MUSHROOM,
            XMaterial.KELP_PLANT,
            XMaterial.SEA_PICKLE,
            // Rare
            XMaterial.PUMPKIN,
            XMaterial.MELON,
            XMaterial.SUGAR_CANE,
            XMaterial.BAMBOO,
            XMaterial.OAK_LEAVES,
            XMaterial.BIRCH_LEAVES,
            XMaterial.SPRUCE_LEAVES,
            XMaterial.GOLD_ORE,
            XMaterial.LAPIS_ORE,
            XMaterial.REDSTONE_ORE,
            XMaterial.DARK_OAK_LEAVES,
            XMaterial.ACACIA_LEAVES,
            XMaterial.SNOW_BLOCK,
            XMaterial.SNOW,
            XMaterial.WHITE_TERRACOTTA,
            XMaterial.ORANGE_TERRACOTTA,
            XMaterial.YELLOW_TERRACOTTA,
            XMaterial.RED_TERRACOTTA,
            XMaterial.LIGHT_GRAY_TERRACOTTA,
            XMaterial.BROWN_TERRACOTTA,
            XMaterial.COARSE_DIRT,
            XMaterial.PODZOL,
            XMaterial.RED_SAND,
            XMaterial.SOUL_SOIL,
            XMaterial.NETHER_GOLD_ORE,
            // Very Rare
            XMaterial.NETHER_WART_BLOCK,
            XMaterial.WARPED_WART_BLOCK,
            XMaterial.DIAMOND_ORE,
            XMaterial.EMERALD_ORE,
            XMaterial.ANCIENT_DEBRIS,
            // TODO Add deepslate, copper ore, tuff, calcite, smooth basalt, amethyst block, amethyst cluster
            // TODO Add deepslate ores, dripstone block, moss block, moss carpet, azalea, flowering azalea
            // TODO Add azalea leaves, flowering azalea leaves, rooted dirt
    };

    private final Material[] materials = new Material[checkedMaterials.length];
    private Material[] customMaterials;

    private final AureliumSkills plugin;
    private final RegionManager regionManager;

    public RegionBlockListener(AureliumSkills plugin) {
        this.plugin = plugin;
        this.regionManager = plugin.getRegionManager();
        SourceManager sourceManager = plugin.getSourceManager();
        for (int i = 0; i < checkedMaterials.length; i++) {
            if (checkedMaterials[i].equals(XMaterial.SUGAR_CANE) && !XMaterial.isNewVersion()) {
                materials[i] = Material.getMaterial("SUGAR_CANE_BLOCK");
            }
            else {
                materials[i] = checkedMaterials[i].parseMaterial();
            }
        }
        customMaterials = new Material[sourceManager.getCustomBlockSet().size()];
        int pos = 0;
        for (XMaterial material : sourceManager.getCustomBlockSet()) {
            customMaterials[pos] = material.parseMaterial();
            pos++;
        }
    }

    public void reloadCustomBlocks() {
        SourceManager sourceManager = plugin.getSourceManager();
        customMaterials = new Material[sourceManager.getCustomBlockSet().size()];
        int pos = 0;
        for (XMaterial material : sourceManager.getCustomBlockSet()) {
            customMaterials[pos] = material.parseMaterial();
            pos++;
        }
    }

    @EventHandler
    public void checkPlace(BlockPlaceEvent event) {
        // Checks if world is blocked
        if (plugin.getWorldManager().isInBlockedCheckWorld(event.getBlock().getLocation())) {
            return;
        }
        // Checks if region is blocked
        if (plugin.isWorldGuardEnabled()) {
            if (plugin.getWorldGuardSupport().isInBlockedCheckRegion(event.getBlock().getLocation())) {
                return;
            }
        }
        // Check block replace
        if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE)) {
            Material material = event.getBlock().getType();
            for (Material checkedMaterial : materials) {
                if (material.equals(checkedMaterial)) {
                    regionManager.addPlacedBlock(event.getBlock());
                    break;
                }
            }
            for (Material checkedMaterial : customMaterials) {
                if (material.equals(checkedMaterial)) {
                    regionManager.addPlacedBlock(event.getBlock());
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onSandFall(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        if (!regionManager.isPlacedBlock(block)) return;
        Material type = block.getType();
        if (type == Material.SAND || type == Material.RED_SAND || type == Material.GRAVEL) {
            Block below = block.getRelative(BlockFace.DOWN);
            if (below.getType() == Material.AIR || below.getType().toString().equals("CAVE_AIR") || below.getType().toString().equals("VOID_AIR")
                    || below.getType() == Material.WATER || below.getType().toString().equals("BUBBLE_COLUMN") || below.getType() == Material.LAVA) {
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
        if (!event.isCancelled()) {
            regionManager.removePlacedBlock(event.getBlock());
            checkSugarCane(event.getBlock(), 0);
            // TODO Check for amethyst cluster, moss carpet, azalea, flowering azalea breaks when supporting block is broken
        }
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

    private void checkSugarCane(Block block, int num) {
        if (num < 20) {
            Block above = block.getRelative(BlockFace.UP);
            if (XBlock.isSugarCane(above.getType())) {
                if (regionManager.isPlacedBlock(above)) {
                    regionManager.removePlacedBlock(above);
                    checkSugarCane(above, num + 1);
                }
            }
        }
    }
}
