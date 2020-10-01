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
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class ForgingLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onForge(InventoryClickEvent event) {
		if (OptionL.isEnabled(Skill.FORGING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.FORGING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			if (event.getClickedInventory() != null) {
				if (event.getClickedInventory().getType().equals(InventoryType.ANVIL)) {
					//Checks if in blocked world
					if (event.getClickedInventory().getLocation() != null) {
						if (AureliumSkills.worldManager.isInBlockedWorld(event.getClickedInventory().getLocation())) {
							return;
						}
					}
					//Checks if in blocked region
					if (AureliumSkills.worldGuardEnabled) {
						if (event.getClickedInventory().getLocation() != null) {
							if (AureliumSkills.worldGuardSupport.isInBlockedRegion(event.getClickedInventory().getLocation())) {
								return;
							}
						}
						else {
							if (AureliumSkills.worldGuardSupport.isInBlockedRegion(event.getWhoClicked().getLocation())) {
								return;
							}
						}
					}
					//Check for permission
					if (!event.getWhoClicked().hasPermission("aureliumskills.forging")) {
						return;
					}
					//Check creative mode disable
					if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
						if (event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)) {
							return;
						}
					}
					if (event.getSlot() == 2) {
						if (event.getAction().equals(InventoryAction.PICKUP_ALL) || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
							if (event.getWhoClicked() instanceof Player) {
								ItemStack addedItem = event.getClickedInventory().getItem(1);
								ItemStack baseItem = event.getClickedInventory().getItem(0);
								Player p = (Player) event.getWhoClicked();
								Skill s = Skill.FORGING;
								AnvilInventory inventory = (AnvilInventory) event.getClickedInventory();
								if (addedItem != null && baseItem != null) {
									if (addedItem.getType().equals(Material.ENCHANTED_BOOK)) {
										if (ItemUtils.isArmor(baseItem.getType())) {
											Leveler.addXp(p, s, inventory.getRepairCost() * OptionL.getXp(Source.COMBINE_ARMOR_PER_LEVEL));
										} else if (ItemUtils.isWeapon(baseItem.getType())) {
											Leveler.addXp(p, s, inventory.getRepairCost() * OptionL.getXp(Source.COMBINE_WEAPON_PER_LEVEL));
										} else if (baseItem.getType().equals(Material.ENCHANTED_BOOK)) {
											Leveler.addXp(p, s, inventory.getRepairCost() * OptionL.getXp(Source.COMBINE_BOOKS_PER_LEVEL));
										} else {
											Leveler.addXp(p, s, inventory.getRepairCost() * OptionL.getXp(Source.COMBINE_TOOL_PER_LEVEL));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
}
