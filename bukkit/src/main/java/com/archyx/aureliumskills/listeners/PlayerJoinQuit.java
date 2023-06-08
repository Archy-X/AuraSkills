package com.archyx.aureliumskills.listeners;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerManager;
import com.archyx.aureliumskills.data.storage.MySqlStorageProvider;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.util.version.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinQuit implements Listener {

	private final AureliumSkills plugin;

	public PlayerJoinQuit(AureliumSkills plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerManager playerManager = plugin.getPlayerManager();
		if (plugin.getStorageProvider() instanceof MySqlStorageProvider) { // Handles MySQL storage
			if (OptionL.getBoolean(Option.MYSQL_ALWAYS_LOAD_ON_JOIN) || playerManager.getPlayerData(player) == null) {
				int loadDelay = OptionL.getInt(Option.MYSQL_LOAD_DELAY);
				if (loadDelay == 0) {
					// Load immediately
					loadPlayerDataAsync(player);
				} else {
					// Delay loading
					new BukkitRunnable() {
						@Override
						public void run() {
							loadPlayerDataAsync(player);
						}
					}.runTaskLater(plugin, loadDelay);
				}
			}
		} else { // Yaml storage
			if (playerManager.getPlayerData(player) == null) {
				loadPlayerDataAsync(player);
			}
		}
		sendUpdateMessage(player); // Attempt to send update message
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.getStorageProvider().save(player);
			}
		}.runTaskAsynchronously(plugin);
		plugin.getActionBar().resetActionBar(player);
	}

	private void loadPlayerDataAsync(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.getStorageProvider().load(player);
			}
		}.runTaskAsynchronously(plugin);
	}

	private void sendUpdateMessage(Player player) {
		// Use a delayed task to give time for permission plugins to load data
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
			if (!OptionL.getBoolean(Option.CHECK_FOR_UPDATES)) {
				return;
			}
			if (!player.hasPermission("aureliumskills.checkupdates")) { // Ensure player has checkupdates permission
				return;
			}
			// Check for updates
			UpdateChecker updateChecker = new UpdateChecker(plugin, plugin.getResourceId());
			updateChecker.getVersion(version -> {
				if (updateChecker.isOutdated(plugin.getDescription().getVersion(), version)) {
					player.sendMessage(AureliumSkills.getPrefix(Lang.getDefaultLanguage()) + ChatColor.WHITE + "New update available! You are on version " + ChatColor.AQUA + plugin.getDescription().getVersion() + ChatColor.WHITE + ", latest version is " + ChatColor.AQUA + version);
					player.sendMessage(AureliumSkills.getPrefix(Lang.getDefaultLanguage()) + ChatColor.WHITE + "Download it on Spigot: " + ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "https://spigotmc.org/resources/" + plugin.getResourceId());
				}
			});
		}, 40L);
	}

}
