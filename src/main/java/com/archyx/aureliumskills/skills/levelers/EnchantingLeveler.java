package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.util.ItemUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantingLeveler extends SkillLeveler implements Listener {

	public EnchantingLeveler(AureliumSkills plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchant(EnchantItemEvent event) {
		if (OptionL.isEnabled(Skill.ENCHANTING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.ENCHANTING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			//Checks if in blocked world
			if (AureliumSkills.worldManager.isInBlockedWorld(event.getEnchantBlock().getLocation())) {
				return;
			}
			//Checks if in blocked region
			if (AureliumSkills.worldGuardEnabled) {
				if (AureliumSkills.worldGuardSupport.isInBlockedRegion(event.getEnchantBlock().getLocation())) {
					return;
				}
			}
			Player p = event.getEnchanter();
			Material mat = event.getItem().getType();
			//Check for permission
			if (!p.hasPermission("aureliumskills.enchanting")) {
				return;
			}
			//Check creative mode disable
			if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
				if (p.getGameMode().equals(GameMode.CREATIVE)) {
					return;
				}
			}
			if (ItemUtils.isArmor(mat)) {
				Leveler.addXp(p, Skill.ENCHANTING, event.getExpLevelCost() * getXp(Source.ARMOR_PER_LEVEL));
			}
			else if (ItemUtils.isWeapon(mat)) {
				Leveler.addXp(p, Skill.ENCHANTING, event.getExpLevelCost() * getXp(Source.WEAPON_PER_LEVEL));
			}
			else if (mat.equals(Material.BOOK)) {
				Leveler.addXp(p, Skill.ENCHANTING, event.getExpLevelCost() * getXp(Source.BOOK_PER_LEVEL));
			}
			else {
				Leveler.addXp(p, Skill.ENCHANTING, event.getExpLevelCost() * getXp(Source.TOOL_PER_LEVEL));
			}
		}
	}
	
}
