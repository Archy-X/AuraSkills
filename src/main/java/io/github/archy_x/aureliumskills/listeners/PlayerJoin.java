package io.github.archy_x.aureliumskills.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.dbassett.skullcreator.SkullCreator;
import io.github.archy_x.aureliumskills.skills.PlayerSkill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.stats.PlayerStat;

public class PlayerJoin implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayJoin(PlayerJoinEvent event) {
		if (!SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
			SkillLoader.playerSkills.put(event.getPlayer().getUniqueId(), new PlayerSkill(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
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
		SkullCreator.blockWithUuid(b, event.getPlayer().getUniqueId());
	}
}
