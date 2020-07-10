package io.github.archy_x.aureliumskills.stats;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class Strength implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			int strength = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.STRENGTH);
			event.setDamage(event.getDamage() + (double) strength * Options.getDoubleOption(Setting.STRENGTH_MODIFIER));
		}
	}
	
}
