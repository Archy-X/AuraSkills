package io.github.archy_x.aureliumskills.skills.levelers;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class ForgingLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onForge(InventoryClickEvent event) {
		if (event.isCancelled() == false) {
			if (event.getClickedInventory() != null) {
				if (event.getClickedInventory().getType().equals(InventoryType.ANVIL)) {
					if (event.getSlot() == 2) {
						if (event.getAction().equals(InventoryAction.PICKUP_ALL) || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
							if (event.getWhoClicked() instanceof Player) {
								ItemStack addedItem = event.getClickedInventory().getItem(1);
								ItemStack baseItem = event.getClickedInventory().getItem(0);
								Player player = (Player) event.getWhoClicked();
								if (addedItem.getType().equals(Material.ENCHANTED_BOOK)) {
									if (baseItem.getType().equals(Material.ENCHANTED_BOOK) == false) {
										EnchantmentStorageMeta meta = (EnchantmentStorageMeta) addedItem.getItemMeta();
										Map<Enchantment, Integer> enchants = meta.getStoredEnchants();
										int levelTotal = 0;
										for (Enchantment e : enchants.keySet()) {
											levelTotal += enchants.get(e);
										}
										if (levelTotal > 0) {
											if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
												SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.FORGING, levelTotal * 20);
												Leveler.playSound(player);
												Leveler.checkLevelUp(player, Skill.FORGING);
												Leveler.sendActionBarMessage(player, Skill.FORGING, levelTotal * 20);
											}
										}
									}
									else {
										EnchantmentStorageMeta meta = (EnchantmentStorageMeta) addedItem.getItemMeta();
										EnchantmentStorageMeta baseMeta = (EnchantmentStorageMeta) baseItem.getItemMeta();
										Map<Enchantment, Integer> enchants = meta.getStoredEnchants();
										Map<Enchantment, Integer> baseEnchants = baseMeta.getStoredEnchants();
										int levelTotal = 0;
										for (Enchantment e : enchants.keySet()) {
											levelTotal += enchants.get(e);
										}
										for (Enchantment e : baseEnchants.keySet()) {
											levelTotal += enchants.get(e);
										}
										if (levelTotal > 0) {
											if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
												SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.FORGING, levelTotal * 10);
												Leveler.playSound(player);
												Leveler.checkLevelUp(player, Skill.FORGING);
												Leveler.sendActionBarMessage(player, Skill.FORGING, levelTotal * 10);
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
	
}
