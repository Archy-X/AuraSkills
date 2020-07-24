package io.github.archy_x.aureliumskills.stats;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.util.ItemUtils;

public class Strength implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			if (Options.getBooleanOption(Setting.STRENGTH_HAND_DAMAGE)) {
				Player player = (Player) event.getDamager();
				int strength = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.STRENGTH);
				event.setDamage(event.getDamage() + (double) strength * Options.getDoubleOption(Setting.STRENGTH_MODIFIER));
			}
			else {
				Player player = (Player) event.getDamager();	
				Material mat = player.getInventory().getItemInMainHand().getType();
				if (ItemUtils.isWeapon(mat) || ItemUtils.isTool(mat)) {
					int strength = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.STRENGTH);
					event.setDamage(event.getDamage() + (double) strength * Options.getDoubleOption(Setting.STRENGTH_MODIFIER));
				}
			}
		}
	}
	
}
