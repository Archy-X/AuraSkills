package io.github.archy_x.aureliumskills.stats;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Strength implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
				if (Options.getBooleanOption(Setting.STRENGTH_HAND_DAMAGE)) {
					int strength = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.STRENGTH);
					event.setDamage(event.getDamage() + (double) strength * Options.getDoubleOption(Setting.STRENGTH_MODIFIER));
				} else {
					Material mat = player.getInventory().getItemInMainHand().getType();
					if (ItemUtils.isWeapon(mat) || ItemUtils.isTool(mat)) {
						int strength = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.STRENGTH);
						event.setDamage(event.getDamage() + (double) strength * Options.getDoubleOption(Setting.STRENGTH_MODIFIER));
					}
				}
			}
		}
		else if (event.getDamager() instanceof Arrow) {
			if (Options.getBooleanOption(Setting.STRENGTH_BOW_DAMAGE)) {
				Arrow arrow = (Arrow) event.getDamager();
				if (arrow.getShooter() instanceof Player) {
					Player player = (Player) arrow.getShooter();
					int strength = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.STRENGTH);
					event.setDamage(event.getDamage() + (double) strength * Options.getDoubleOption(Setting.STRENGTH_MODIFIER));
				}
			}
		}
	}
	
}
