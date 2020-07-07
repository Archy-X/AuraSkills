package io.github.archy_x.aureliumskills.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockBreak implements Listener {

	private Plugin plugin;
	
	public BlockBreak(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().hasMetadata("skillsPlaced")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					event.getBlock().removeMetadata("skillsPlaced", plugin);
				}
			}.runTaskLater(plugin, 1L);
		}
	}
}
