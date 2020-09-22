package com.archyx.aureliumskills.listeners;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.PlayerSkillInstance;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.util.UpdateChecker;
import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoin implements Listener {

	private final Plugin plugin;

	public PlayerJoin(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
			SkillLoader.playerSkills.put(event.getPlayer().getUniqueId(), new PlayerSkill(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
			AureliumSkills.leaderboard.queueAdd(new PlayerSkillInstance(SkillLoader.playerSkills.get(event.getPlayer().getUniqueId())));
		}
		else {
			SkillLoader.playerSkills.get(event.getPlayer().getUniqueId()).setPlayerName(event.getPlayer().getName());
		}
		if (!SkillLoader.playerStats.containsKey(event.getPlayer().getUniqueId())) {
			SkillLoader.playerStats.put(event.getPlayer().getUniqueId(), new PlayerStat(event.getPlayer().getUniqueId()));
		}
		Location playerLoc = event.getPlayer().getLocation();
		Location loc = new Location(playerLoc.getWorld(), playerLoc.getX(), 0, playerLoc.getZ());
		Block b = loc.getBlock();
		BlockState state = b.getState();
		SkullCreator.blockWithUuid(b, event.getPlayer().getUniqueId());
		state.update(true);
		if (event.getPlayer().isOp()) {
			Player player = event.getPlayer();
			//Check for updates
			new UpdateChecker(plugin, 81069).getVersion(version -> {
				if (!plugin.getDescription().getVersion().contains("Pre-Release")) {
					if (!plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
						player.sendMessage(AureliumSkills.tag + ChatColor.WHITE + "New update available! You are on version " + ChatColor.AQUA + plugin.getDescription().getVersion() + ChatColor.WHITE + ", latest version is " + ChatColor.AQUA + version);
						player.sendMessage(AureliumSkills.tag + ChatColor.WHITE + "Download it on Spigot: " + ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "http://spigotmc.org/resources/81069");
					}
				}
			});
		}
	}
}
