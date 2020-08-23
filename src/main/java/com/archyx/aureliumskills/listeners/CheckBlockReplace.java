package com.archyx.aureliumskills.listeners;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.skills.levelers.FarmingLeveler;
import com.archyx.aureliumskills.util.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CheckBlockReplace implements Listener {

    private final XMaterial[] checkedMaterials = new XMaterial[] {
            //Ultra Common
            XMaterial.COBBLESTONE,
            XMaterial.DIRT,
            XMaterial.NETHERRACK,
            XMaterial.STONE,
            //Common
            XMaterial.SAND,
            XMaterial.GRAVEL,
            XMaterial.GRASS_BLOCK,
            XMaterial.OAK_LOG,
            XMaterial.BIRCH_LOG,
            XMaterial.SPRUCE_LOG,
            XMaterial.GRANITE,
            XMaterial.DIORITE,
            XMaterial.ANDESITE,
            //Uncommon
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
            //Rare
            XMaterial.PUMPKIN,
            XMaterial.MELON,
            XMaterial.SUGAR_CANE,
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
            //Very Rare
            XMaterial.NETHER_WART_BLOCK,
            XMaterial.WARPED_WART_BLOCK,
            XMaterial.DIAMOND_ORE,
            XMaterial.EMERALD_ORE,
            XMaterial.ANCIENT_DEBRIS,
    };

    private Material[] materials = new Material[checkedMaterials.length];

    private final Plugin plugin;
    private NumberFormat nf = new DecimalFormat("#.####");

    public CheckBlockReplace(Plugin plugin) {
        this.plugin = plugin;
        for (int i = 0; i < checkedMaterials.length; i++) {
            if (checkedMaterials[i].equals(XMaterial.SUGAR_CANE) && !XMaterial.isNewVersion()) {
                materials[i] = Material.getMaterial("SUGAR_CANE_BLOCK");
            }
            else {
                materials[i] = checkedMaterials[i].parseMaterial();
            }
        }
    }

    @EventHandler
    public void checkPlace(BlockPlaceEvent event) {
        //Checks if world is blocked
        if (AureliumSkills.worldManager.isInBlockedCheckWorld(event.getBlock().getLocation())) {
            return;
        }
        //Checks if region is blocked
        if (AureliumSkills.worldGuardEnabled) {
            if (AureliumSkills.worldGuardSupport.isInBlockedCheckRegion(event.getBlock().getLocation())) {
                return;
            }
        }
        //Check block replace
        if (Options.checkBlockReplace) {
            Material material = event.getBlock().getType();
            for (Material checkedMaterial : materials) {
                if (material.equals(checkedMaterial)) {
                    event.getBlock().setMetadata("skillsPlaced", new FixedMetadataValue(plugin, true));
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            if (event.getBlock().hasMetadata("skillsPlaced")) {
                event.getBlock().removeMetadata("skillsPlaced", plugin);
            }
            checkSugarCane(event.getBlock(), 0);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.hasMetadata("skillsPlaced")) {
                block.getRelative(event.getDirection()).setMetadata("skillsPlaced", new FixedMetadataValue(plugin, true));
            }
        }
        event.getBlock().getRelative(event.getDirection()).removeMetadata("skillsPlaced", plugin);
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        Block lastBlock = event.getBlock();
        for (Block block : event.getBlocks()) {
            if (block.hasMetadata("skillsPlaced")) {
                block.getRelative(event.getDirection()).setMetadata("skillsPlaced", new FixedMetadataValue(plugin, true));
                if (block.getLocation().distanceSquared(event.getBlock().getLocation()) > lastBlock.getLocation().distanceSquared(event.getBlock().getLocation())) {
                    lastBlock = block;
                }
            }
        }
        lastBlock.removeMetadata("skillsPlaced", plugin);
    }

    private void checkSugarCane(Block block, int num) {
        if (num < 20) {
            Block above = block.getRelative(BlockFace.UP);
            if (FarmingLeveler.isSugarCane(above.getType())) {
                if (above.hasMetadata("skillsPlaced")) {
                    above.removeMetadata("skillsPlaced", plugin);
                    checkSugarCane(above, num + 1);
                }
            }
        }
    }
}
