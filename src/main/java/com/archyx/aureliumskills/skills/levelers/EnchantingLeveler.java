package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.util.item.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantingLeveler extends SkillLeveler implements Listener {

	public EnchantingLeveler(AureliumSkills plugin) {
		super(plugin, Ability.ENCHANTER);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchant(EnchantItemEvent event) {
		if (OptionL.isEnabled(Skills.ENCHANTING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.ENCHANTING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			Player p = event.getEnchanter();
			if (blockXpGainLocation(event.getEnchantBlock().getLocation(), p)) return;
			Material mat = event.getItem().getType();
			if (blockXpGainPlayer(p)) return;
			if (ItemUtils.isArmor(mat)) {
				plugin.getLeveler().addXp(p, Skills.ENCHANTING, event.getExpLevelCost() * getXp(Source.ARMOR_PER_LEVEL));
			}
			else if (ItemUtils.isWeapon(mat)) {
				plugin.getLeveler().addXp(p, Skills.ENCHANTING, event.getExpLevelCost() * getXp(Source.WEAPON_PER_LEVEL));
			}
			else if (mat.equals(Material.BOOK)) {
				plugin.getLeveler().addXp(p, Skills.ENCHANTING, event.getExpLevelCost() * getXp(Source.BOOK_PER_LEVEL));
			}
			else {
				plugin.getLeveler().addXp(p, Skills.ENCHANTING, event.getExpLevelCost() * getXp(Source.TOOL_PER_LEVEL));
			}
		}
	}
	
}
