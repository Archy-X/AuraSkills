package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.Source;

public class DefenseLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (Options.isEnabled(Skill.DEFENSE)) {
			if (event.isCancelled() == false) {
				if (event.getEntity() instanceof Player) {
					Player p = (Player) event.getEntity();
					Skill s = Skill.DEFENSE;
					double d = event.getDamage();
					if (event.getDamager() instanceof Player) {
						Leveler.addXp(p, s, d * Options.getXpAmount(Source.PLAYER_DAMAGE));
					}
					else {
						Leveler.addXp(p, s, d * Options.getXpAmount(Source.MOB_DAMAGE));
					}
				}
			}
		}
	}
}
