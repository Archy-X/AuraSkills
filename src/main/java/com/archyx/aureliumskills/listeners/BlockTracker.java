package com.archyx.aureliumskills.listeners;

import com.archyx.aureliumskills.util.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class BlockTracker implements Listener {

    private Plugin plugin;

    public BlockTracker(Plugin plugin) {
        this.plugin = plugin;
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

    public boolean isCheckedBlock(BlockState state) {
        Material mat = state.getType();
        return mat.equals(XMaterial.OAK_LOG.parseMaterial()) || mat.equals(XMaterial.BIRCH_LOG.parseMaterial()) || mat.equals(XMaterial.SPRUCE_LOG.parseMaterial())
                || mat.equals(Material.PUMPKIN) || mat.equals(XMaterial.JUNGLE_LOG.parseMaterial()) || mat.equals(XMaterial.DARK_OAK_LOG.parseMaterial()) ||
                mat.equals(XMaterial.ACACIA_LOG.parseMaterial()) ||
                mat.equals(Material.MELON) || mat.equals(Material.COAL_ORE) || mat.equals(Material.IRON_ORE) ||
                mat.equals(Material.GOLD_ORE) || mat.equals(Material.DIAMOND_ORE) || mat.equals(Material.EMERALD_ORE) ||
                mat.equals(Material.STONE) || mat.equals(Material.DIRT) || mat.equals(Material.SAND) ||
                mat.equals(Material.GRAVEL) || mat.equals(Material.COBBLESTONE) || mat.equals(Material.REDSTONE_ORE) ||
                mat.equals(Material.LAPIS_ORE) || mat.equals(Material.CLAY) || mat.equals(Material.GRASS) ||
                mat.equals(XMaterial.MYCELIUM.parseMaterial()) || mat.equals(Material.SOUL_SAND) || mat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial()) ||
                mat.equals(XMaterial.LAPIS_ORE.parseMaterial()) || mat.equals(XMaterial.CLAY.parseMaterial()) || mat.equals(XMaterial.GRASS_BLOCK.parseMaterial());
    }

}
