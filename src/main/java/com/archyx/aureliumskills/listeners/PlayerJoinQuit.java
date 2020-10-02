package com.archyx.aureliumskills.listeners;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinQuit implements Listener {

	private final AureliumSkills plugin;

	public PlayerJoinQuit(AureliumSkills plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			SkillLoader.playerSkills.put(player.getUniqueId(), new PlayerSkill(player.getUniqueId(), player.getName()));
			AureliumSkills.leaderboard.queueAdd(new PlayerSkillInstance(SkillLoader.playerSkills.get(player.getUniqueId())));
		} else {
			SkillLoader.playerSkills.get(player.getUniqueId()).setPlayerName(player.getName());
		}
		if (!SkillLoader.playerStats.containsKey(player.getUniqueId())) {
			SkillLoader.playerStats.put(player.getUniqueId(), new PlayerStat(player.getUniqueId()));
		}
		//Load player skull
		Location playerLoc = player.getLocation();
		Location loc = new Location(playerLoc.getWorld(), playerLoc.getX(), 0, playerLoc.getZ());
		Block b = loc.getBlock();
		BlockState state = b.getState();
		SkullCreator.blockWithUuid(b, player.getUniqueId());
		state.update(true);
		//Update message
		if (player.isOp()) {
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
