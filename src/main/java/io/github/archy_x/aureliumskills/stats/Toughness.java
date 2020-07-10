package io.github.archy_x.aureliumskills.stats;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class Toughness implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		int toughness = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.TOUGHNESS);
		player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue((double) toughness * Options.getDoubleOption(Setting.TOUGHNESS_MODIFIER));
	}
	
	public static void reload(Player player) {
		int toughness = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.TOUGHNESS);
		player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue((double) toughness * Options.getDoubleOption(Setting.TOUGHNESS_MODIFIER));
	}
}
