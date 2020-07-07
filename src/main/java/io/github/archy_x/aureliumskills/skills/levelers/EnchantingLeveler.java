package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class EnchantingLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchant(EnchantItemEvent event) {
		if (event.isCancelled() == false) {
			Player player = event.getEnchanter();
			if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
				SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ENCHANTING, event.getExpLevelCost() * 2 * event.getEnchantsToAdd().size());
				Leveler.playSound(player);
				Leveler.checkLevelUp(player, Skill.ENCHANTING);
				Leveler.sendActionBarMessage(player, Skill.ENCHANTING, event.getExpLevelCost() * 2 * event.getEnchantsToAdd().size());
			}
		}
	}
	
}
