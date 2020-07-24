package io.github.archy_x.aureliumskills.stats;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Toughness implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (Options.getBooleanOption(Setting.RESET_ARMOR_ATTRIBUTE)) {
			event.getPlayer().getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
				double toughness = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.TOUGHNESS) * Options.getDoubleOption(Setting.TOUGHNESS_MODIFIER);
				event.setDamage(event.getDamage() * (1 - (-1.0 * Math.pow(1.01, -1.0 * toughness) + 1)));
			}
		}
	}
}
