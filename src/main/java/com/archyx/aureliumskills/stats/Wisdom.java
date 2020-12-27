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
			if (anvil.getRepairCost() - (int) (stat.getStatLevel(Stat.WISDOM) * OptionL.getDouble(Option.WISDOM_ANVIL_COST_MODIFIER)) > 0) {
				anvil.setRepairCost(anvil.getRepairCost() - (int) (stat.getStatLevel(Stat.WISDOM) * OptionL.getDouble(Option.WISDOM_ANVIL_COST_MODIFIER)));
			}
			else {
				anvil.setRepairCost(1);
			}
		}
	}
}
