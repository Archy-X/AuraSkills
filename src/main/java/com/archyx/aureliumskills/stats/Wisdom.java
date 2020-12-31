package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.AnvilInventory;

public class Wisdom implements Listener {

	private final AureliumSkills plugin;

	public Wisdom(AureliumSkills plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		Player player = event.getPlayer();
		//Check for disabled world
		if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
			return;
		}
		if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
			PlayerStat stat = SkillLoader.playerStats.get(player.getUniqueId());
			event.setAmount((int) (event.getAmount() * (1 + (stat.getStatLevel(Stat.WISDOM) * OptionL.getDouble(Option.WISDOM_EXPERIENCE_MODIFIER)))));
		}
	}
	
	@EventHandler
	public void onAnvilPrepare(PrepareAnvilEvent event) {
		PlayerStat stat = null;
		//Finds the viewer with the highest wisdom level
		for (HumanEntity entity : event.getViewers()) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				//Check for disabled world
				if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
					return;
				}
				PlayerStat checkedStat = SkillLoader.playerStats.get(player.getUniqueId());
				if (checkedStat != null) {
					if (stat == null) {
						stat = checkedStat;
					} else if (stat.getStatLevel(Stat.WISDOM) < checkedStat.getStatLevel(Stat.WISDOM)) {
						stat = checkedStat;
					}
				}
			}
		}
		if (stat != null) {
			AnvilInventory anvil = event.getInventory();
			double wisdom = stat.getStatLevel(Stat.WISDOM) * OptionL.getDouble(Option.WISDOM_ANVIL_COST_MODIFIER);
			int cost = (int) Math.round(anvil.getRepairCost() * (1 - (-1.0 * Math.pow(1.025, -1.0 * wisdom) + 1)));
			if (cost > 0) {
				anvil.setRepairCost(cost);
			} else {
				anvil.setRepairCost(1);
			}
		}
	}
}
