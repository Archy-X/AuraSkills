package io.github.archy_x.aureliumskills.listeners;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Options;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import io.github.archy_x.aureliumskills.util.XMaterial;

public class BlockPlace implements Listener {

	private Plugin plugin;
	
	public BlockPlace(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
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
		//Checks if check block replace is enabled
		if (Options.checkBlockReplace) {
			Material mat = event.getBlock().getType();
			if (mat.equals(XMaterial.OAK_LOG.parseMaterial()) || mat.equals(XMaterial.BIRCH_LOG.parseMaterial()) || mat.equals(XMaterial.SPRUCE_LOG.parseMaterial())
					|| mat.equals(Material.PUMPKIN) || mat.equals(XMaterial.JUNGLE_LOG.parseMaterial()) || mat.equals(XMaterial.DARK_OAK_LOG.parseMaterial()) ||
					mat.equals(XMaterial.ACACIA_LOG.parseMaterial()) ||
					mat.equals(Material.MELON) || mat.equals(Material.COAL_ORE) || mat.equals(Material.IRON_ORE) ||
					mat.equals(Material.GOLD_ORE) || mat.equals(Material.DIAMOND_ORE) || mat.equals(Material.EMERALD_ORE) ||
					mat.equals(Material.STONE) || mat.equals(Material.DIRT) || mat.equals(Material.SAND) ||
					mat.equals(Material.GRAVEL) || mat.equals(Material.COBBLESTONE) || mat.equals(Material.REDSTONE_ORE) ||
					mat.equals(Material.LAPIS_ORE) || mat.equals(Material.CLAY) || mat.equals(Material.GRASS) ||
					mat.equals(XMaterial.MYCELIUM.parseMaterial()) || mat.equals(Material.SOUL_SAND) || mat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial()) ||
					mat.equals(XMaterial.LAPIS_ORE.parseMaterial()) || mat.equals(XMaterial.CLAY.parseMaterial()) || mat.equals(XMaterial.GRASS_BLOCK.parseMaterial())) {
				event.getBlock().setMetadata("skillsPlaced", new FixedMetadataValue(plugin, true));
			}
		}
	}
	
}
