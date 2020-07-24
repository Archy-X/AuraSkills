package io.github.archy_x.aureliumskills.skills.abilities;

import org.bukkit.entity.Player;

public interface RightClickAbility {
	
	public void start(Player player);
	
	public void update(Player player);
	
	public void stop(Player player);
}
