package io.github.archy_x.aureliumskills.stats;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.AnvilInventory;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class Wisdom implements Listener {

	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		if (SkillLoader.playerStats.containsKey(event.getPlayer().getUniqueId())) {
			PlayerStat stat = SkillLoader.playerStats.get(event.getPlayer().getUniqueId());
			event.setAmount((int) (event.getAmount() * (1 + (stat.getStatLevel(Stat.WISDOM) * Options.getDoubleOption(Setting.EXPERIENCE_MODIFIER)))));
		}
	}
	
	@EventHandler
	public void onAnvilPrepare(PrepareAnvilEvent event) {
		PlayerStat stat = null;
		//Finds the viewer with the highest wisdom level
		for (HumanEntity entity : event.getViewers()) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
					if (stat == null) {
						stat = SkillLoader.playerStats.get(player.getUniqueId());
					}
					else {
						if (stat.getStatLevel(Stat.WISDOM) < SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.WISDOM)) {
							stat = SkillLoader.playerStats.get(player.getUniqueId());
						}
					}
				}
			}
		}
		if (stat != null) {
			AnvilInventory anvil = event.getInventory();
			if (anvil.getRepairCost() - (int) (stat.getStatLevel(Stat.WISDOM) * Options.getDoubleOption(Setting.ANVIL_COST_MODIFIER)) > 0) {
				anvil.setRepairCost(anvil.getRepairCost() - (int) (stat.getStatLevel(Stat.WISDOM) * Options.getDoubleOption(Setting.ANVIL_COST_MODIFIER)));
			}
			else {
				anvil.setRepairCost(1);
			}
		}
	}
}
