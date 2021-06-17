package com.archyx.aureliumskills.skills.forging;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.leveler.SkillLeveler;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.util.item.ItemUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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
				if (!(event.getWhoClicked() instanceof Player)) return;
				Player player = (Player) event.getWhoClicked();
				ClickType click = event.getClick();
				// Only allow right and left clicks if inventory full
				if (click != ClickType.LEFT && click != ClickType.RIGHT && ItemUtils.isInventoryFull(player)) return;
				if (event.getResult() != Event.Result.ALLOW) return; // Make sure the click was successful
				if (inventory.getType().equals(InventoryType.ANVIL)) {
					if (event.getSlot() == 2) {
						InventoryAction action = event.getAction();
						if (action == InventoryAction.PICKUP_ALL || action == InventoryAction.MOVE_TO_OTHER_INVENTORY
							|| action == InventoryAction.PICKUP_HALF) {
							ItemStack addedItem = inventory.getItem(1);
							ItemStack baseItem = inventory.getItem(0);
							if (inventory.getLocation() != null) {
								if (blockXpGainLocation(inventory.getLocation(), player)) return;
							} else {
								if (blockXpGainLocation(event.getWhoClicked().getLocation(), player)) return;
							}
							if (blockXpGainPlayer(player)) return;
							Skill s = Skills.FORGING;
							AnvilInventory anvil = (AnvilInventory) inventory;
							if (addedItem != null && baseItem != null) {
								if (addedItem.getType().equals(Material.ENCHANTED_BOOK)) {
									if (ItemUtils.isArmor(baseItem.getType())) {
										plugin.getLeveler().addXp(player, s, anvil.getRepairCost() * getXp(ForgingSource.COMBINE_ARMOR_PER_LEVEL));
									} else if (ItemUtils.isWeapon(baseItem.getType())) {
										plugin.getLeveler().addXp(player, s, anvil.getRepairCost() * getXp(ForgingSource.COMBINE_WEAPON_PER_LEVEL));
									} else if (baseItem.getType().equals(Material.ENCHANTED_BOOK)) {
										plugin.getLeveler().addXp(player, s, anvil.getRepairCost() * getXp(ForgingSource.COMBINE_BOOKS_PER_LEVEL));
									} else {
										plugin.getLeveler().addXp(player, s, anvil.getRepairCost() * getXp(ForgingSource.COMBINE_TOOL_PER_LEVEL));
									}
								}
							}

						}
					}
				} else if (inventory.getType().toString().equals("GRINDSTONE")) {
					if (event.getSlotType() != InventoryType.SlotType.RESULT) return;
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
						for (Map.Entry<Enchantment, Integer> entry : topItem.getEnchantments().entrySet()) {
							if (!entry.getKey().equals(Enchantment.BINDING_CURSE) && !entry.getKey().equals(Enchantment.VANISHING_CURSE)) {
								totalLevel += entry.getValue();
							}
						}
					}
					ItemStack bottomItem = inventory.getItem(1); // Get item in bottom slot
					if (bottomItem != null) {
						for (Map.Entry<Enchantment, Integer> entry : bottomItem.getEnchantments().entrySet()) {
							if (!entry.getKey().equals(Enchantment.BINDING_CURSE) && !entry.getKey().equals(Enchantment.VANISHING_CURSE)) {
								totalLevel += entry.getValue();
							}
						}
					}
					plugin.getLeveler().addXp(player, Skills.FORGING, totalLevel * getXp(ForgingSource.GRINDSTONE_PER_LEVEL));
				}
			}
		}
	}

}
