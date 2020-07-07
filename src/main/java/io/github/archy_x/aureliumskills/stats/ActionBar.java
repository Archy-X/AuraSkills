package io.github.archy_x.aureliumskills.stats;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.archy_x.aureliumskills.skills.SkillLoader;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBar {

	private Plugin plugin;
	
	public ActionBar(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public static HashMap<UUID, Boolean> isGainingXp = new HashMap<UUID, Boolean>();
	public static HashMap<UUID, Integer> timer = new HashMap<UUID, Integer>();
	public static HashMap<UUID, Integer> currentAction = new HashMap<UUID, Integer>();
	
	public void startUpdateActionBar() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (timer.containsKey(player.getUniqueId())) {
						if (timer.get(player.getUniqueId()) != 0) {
							timer.put(player.getUniqueId(), timer.get(player.getUniqueId()) - 1);
						}
					}
					else {
						timer.put(player.getUniqueId(), 0);
					}
					if (currentAction.containsKey(player.getUniqueId()) == false) {
						currentAction.put(player.getUniqueId(), 0);
					}
					if (isGainingXp.containsKey(player.getUniqueId())) {
						if (isGainingXp.get(player.getUniqueId()) == false) {
							player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "" + (int) (player.getHealth() * 5) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 5) + " HP" + 
									"                " + ChatColor.AQUA + 20 + "/" + (20 + 2 * SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.WISDOM)) + " Mana"));
						}
					}
					else {
						isGainingXp.put(player.getUniqueId(), false);
					}
				}
			}
		}, 0L, 2L);
	}
	
}
