package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class DefenseLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled() == false) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
					SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.DEFENSE, event.getDamage());
					Leveler.playSound(player);
					Leveler.checkLevelUp(player, Skill.DEFENSE);
					Leveler.sendActionBarMessage(player, Skill.DEFENSE, event.getDamage());
				}
			}
		}
	}
}
