package com.archyx.aureliumskills.listeners;

import com.archyx.aureliumskills.skills.levelers.FarmingLeveler;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockBreak implements Listener {

	private Plugin plugin;
	
	public BlockBreak(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			if (event.getBlock().hasMetadata("skillsPlaced")) {
				event.getBlock().removeMetadata("skillsPlaced", plugin);
			}
			checkSugarCane(event.getBlock(), 0);
		}
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
