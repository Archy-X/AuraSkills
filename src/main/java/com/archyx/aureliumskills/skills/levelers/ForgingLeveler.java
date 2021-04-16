package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.util.item.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ForgingLeveler extends SkillLeveler implements Listener {

	public ForgingLeveler(AureliumSkills plugin) {
		super(plugin, Ability.FORGER);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onForge(InventoryClickEvent event) {
		if (OptionL.isEnabled(Skills.FORGING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.FORGING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			Inventory inventory = event.getClickedInventory();
			if (inventory != null) {
				if (inventory.getType().equals(InventoryType.ANVIL)) {
					if (event.getSlot() == 2) {
						if (event.getAction().equals(InventoryAction.PICKUP_ALL) || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
							if (event.getWhoClicked() instanceof Player) {
								ItemStack addedItem = inventory.getItem(1);
								ItemStack baseItem = inventory.getItem(0);
								Player p = (Player) event.getWhoClicked();
								if (inventory.getLocation() != null) {
									if (blockXpGainLocation(inventory.getLocation(), p)) return;
								} else {
									if (blockXpGainLocation(event.getWhoClicked().getLocation(), p)) return;
								}
								if (blockXpGainPlayer(p)) return;
								Skill s = Skills.FORGING;
								AnvilInventory anvil = (AnvilInventory) inventory;
								if (addedItem != null && baseItem != null) {
									if (addedItem.getType().equals(Material.ENCHANTED_BOOK)) {
										if (ItemUtils.isArmor(baseItem.getType())) {
											plugin.getLeveler().addXp(p, s, anvil.getRepairCost() * getXp(Source.COMBINE_ARMOR_PER_LEVEL));
										} else if (ItemUtils.isWeapon(baseItem.getType())) {
											plugin.getLeveler().addXp(p, s, anvil.getRepairCost() * getXp(Source.COMBINE_WEAPON_PER_LEVEL));
										} else if (baseItem.getType().equals(Material.ENCHANTED_BOOK)) {
											plugin.getLeveler().addXp(p, s, anvil.getRepairCost() * getXp(Source.COMBINE_BOOKS_PER_LEVEL));
										} else {
											plugin.getLeveler().addXp(p, s, anvil.getRepairCost() * getXp(Source.COMBINE_TOOL_PER_LEVEL));
										}
									}
								}
							}
						}
					}
				} else if (inventory.getType().toString().equals("GRINDSTONE")) {
					if (event.getSlotType() != InventoryType.SlotType.RESULT) return;
					if (!(event.getWhoClicked() instanceof Player)) return;
					Player player = (Player) event.getWhoClicked();
					if (inventory.getLocation() != null) {
						if (blockXpGainLocation(inventory.getLocation(), player)) return;
					} else {
						if (blockXpGainLocation(event.getWhoClicked().getLocation(), player)) return;
					}
					if (blockXpGainPlayer(player)) return;
					// Calculate total level
					int totalLevel = 0;
					ItemStack topItem = inventory.getItem(0); // Get item in top slot
					if (topItem != null) {
						for (Integer level : topItem.getEnchantments().values()) {
							totalLevel += level;
						}
					}
					ItemStack bottomItem = inventory.getItem(1); // Get item in bottom slot
					if (bottomItem != null) {
						for (Integer level : bottomItem.getEnchantments().values()) {
							totalLevel += level;
						}
					}
					plugin.getLeveler().addXp(player, Skills.FORGING, totalLevel * getXp(Source.GRINDSTONE_PER_LEVEL));
				}
			}
		}
	}
	
}
