package dev.aurelium.auraskills.bukkit.util;

import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemUtils {

	public static boolean getAndAddEnchant(String enchantName, int level, ItemStack item, ItemMeta meta) {
		Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantName.toLowerCase(Locale.ROOT)));
		if (enchantment != null) {
			if (item.getType() == Material.ENCHANTED_BOOK && meta instanceof EnchantmentStorageMeta esm) {
				esm.addStoredEnchant(enchantment, level, true);
				item.setItemMeta(esm);
			} else {
				meta.addEnchant(enchantment, level, true);
				item.setItemMeta(meta);
			}
			return true;
		} else {
			return false;
		}
	}

	public static void giveBlockLoot(Player player, LootDropEvent event) {
		if (event.isToInventory()) {
			Map<Integer, ItemStack> notAdded = player.getInventory().addItem(event.getItem());
			for (ItemStack leftover : notAdded.values()) {
				dropItem(event.getLocation(), leftover);
			}
		} else {
			dropItem(event.getLocation(), event.getItem());
		}
	}

	private static void dropItem(Location location, ItemStack itemStack) {
		World world = location.getWorld();
		if (world != null) {
			world.dropItem(location, itemStack);
		}
	}

	public static boolean hasTelekinesis(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) return false;
		for (Enchantment enchant : item.getEnchantments().keySet()) {
			if (enchant.getKey().getKey().equals("telekinesis")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isArmor(Material material) {
		String materialName = material.name().toLowerCase(Locale.ROOT);
		return materialName.contains("helmet") || materialName.contains("chestplate") || materialName.contains("leggings") || materialName.contains("boots") || materialName.equals("elytra");
	}

	public static boolean isWeapon(Material material) {
		String materialName = material.name().toLowerCase(Locale.ROOT);
		return materialName.contains("sword") || materialName.equals("bow") || materialName.equals("trident") || materialName.equals("crossbow");
	}

	public static boolean isTool(Material material) {
		String materialName = material.name().toLowerCase(Locale.ROOT);
		return materialName.contains("pickaxe") || materialName.contains("axe") || materialName.contains("hoe") || materialName.contains("shovel") || materialName.contains("spade")
				|| materialName.equals("shears") || materialName.equals("fishing_rod") || materialName.equals("flint_and_steel") || materialName.equals("shield")
				|| materialName.contains("on_a_stick");
	}

	public static boolean isAxe(Material material) {
		String materialName = material.name().toLowerCase(Locale.ROOT);
		return materialName.contains("_axe");
	}

	public static boolean isPickaxe(Material material) {
		return material.name().toLowerCase(Locale.ROOT).contains("pickaxe");
	}

	public static boolean isDurable(Material material) {
		return isArmor(material) || isWeapon(material) || isTool(material);
	}

	public static List<String> formatLore(List<String> input) {
		List<String> lore = new ArrayList<>();
		for (String entry : input) {
			lore.addAll(Arrays.asList(entry.split("(\\u005C\\u006E)|(\\n)")));
		}
		return lore;
	}

	public static ReadWriteNBT getCompound(ReadWriteNBT root, String name) {
		ReadWriteNBT compound = root.getCompound(name);
		if (compound == null) {
			compound = root.getOrCreateCompound(name);
		}
		return compound;
	}

	public static ReadWriteNBT getRootCompound(ReadWriteNBT item) {
		ReadWriteNBT compound = item.getCompound("AuraSkills");
		if (compound == null) {
			compound = item.getOrCreateCompound("AuraSkills");
		}
		return compound;
	}

	public static ReadWriteNBT getLegacyRootCompound(ReadWriteNBT item) {
		ReadWriteNBT compound = item.getCompound("AureliumSkills");
		if (compound == null) {
			compound = item.getOrCreateCompound("AureliumSkills");
		}
		return compound;
	}

	public static ReadWriteNBT getModifiersCompound(ReadWriteNBT item) {
		return getCompound(getRootCompound(item), "Modifiers");
	}

	public static ReadWriteNBT getLegacyModifiersCompound(ReadWriteNBT item) {
		return getCompound(getLegacyRootCompound(item), "Modifiers");
	}

	public static ReadWriteNBT getModifiersTypeCompound(ReadWriteNBT item, ModifierType type) {
		return getCompound(getModifiersCompound(item), TextUtil.capitalize(type.name().toLowerCase(Locale.ROOT)));
	}

	public static ReadWriteNBT getLegacyModifiersTypeCompound(ReadWriteNBT item, ModifierType type) {
		return getCompound(getLegacyModifiersCompound(item), TextUtil.capitalize(type.name().toLowerCase(Locale.ROOT)));
	}

	public static ReadWriteNBT getRequirementsCompound(ReadWriteNBT item) {
		return getCompound(getRootCompound(item), "Requirements");
	}

	public static ReadWriteNBT getRequirementsTypeCompound(ReadWriteNBT item, ModifierType type) {
		return getCompound(getRequirementsCompound(item), TextUtil.capitalize(type.name().toLowerCase(Locale.ROOT)));
	}

	public static ReadWriteNBT getLegacyRequirementsCompound(ReadWriteNBT item) {
		return getCompound(getLegacyRootCompound(item), "Requirements");
	}

	public static ReadWriteNBT getLegacyRequirementsTypeCompound(ReadWriteNBT item, ModifierType type) {
		return getCompound(getLegacyRequirementsCompound(item), TextUtil.capitalize(type.name().toLowerCase(Locale.ROOT)));
	}

	public static ReadWriteNBT getMultipliersCompound(ReadWriteNBT item) {
		return getCompound(getRootCompound(item), "Multipliers");
	}

	public static ReadWriteNBT getMultipliersTypeCompound(ReadWriteNBT item, ModifierType type) {
		return getCompound(getMultipliersCompound(item), TextUtil.capitalize(type.name().toLowerCase(Locale.ROOT)));
	}

	public static ReadWriteNBT getLegacyMultipliersCompound(ReadWriteNBT item) {
		return getCompound(getLegacyRootCompound(item), "Multipliers");
	}

	public static ReadWriteNBT getLegacyMultipliersTypeCompound(ReadWriteNBT item, ModifierType type) {
		return getCompound(getLegacyMultipliersCompound(item), TextUtil.capitalize(type.name().toLowerCase(Locale.ROOT)));
	}

	public static boolean isInventoryFull(Player player) {
		for (ItemStack item : player.getInventory().getStorageContents()) {
			if (item == null || item.getType() == Material.AIR) {
				return false;
			}
		}
		return true;
	}

	@Nullable
	public static ItemStack addItemToInventory(Player player, ItemStack item) {
		PlayerInventory inventory = player.getInventory();
		int amountRemaining = item.getAmount();
		for (int slot = 0; slot < 36; slot++) {
			ItemStack slotItem = inventory.getItem(slot);
			if (amountRemaining > 0) {
				if (slotItem == null || slotItem.getType() == Material.AIR) {
					if (amountRemaining > item.getMaxStackSize() && item.getMaxStackSize() != -1) {
						ItemStack maxStackItem = item.clone();
						maxStackItem.setAmount(item.getMaxStackSize());
						inventory.setItem(slot, maxStackItem);
						amountRemaining -= item.getMaxStackSize();
					} else {
						ItemStack addedItem = item.clone();
						addedItem.setAmount(amountRemaining);
						inventory.setItem(slot, addedItem);
						amountRemaining = 0;
					}
				} else if (slotItem.isSimilar(item)) {
					int amountAdded = Math.min(amountRemaining, slotItem.getMaxStackSize() - slotItem.getAmount());
					slotItem.setAmount(slotItem.getAmount() + amountAdded);
					amountRemaining -= amountAdded;
				}
			}
		}
		if (amountRemaining > 0) {
			ItemStack leftoverItem = item.clone();
			leftoverItem.setAmount(amountRemaining);
			return leftoverItem;
		}
		return null;
	}

	public static boolean canAddItemToInventory(Player player, ItemStack item) {
		PlayerInventory inventory = player.getInventory();
		int amountRemaining = item.getAmount();
		for (int slot = 0; slot < 36; slot++) {
			ItemStack slotItem = inventory.getItem(slot);
			if (amountRemaining > 0) {
				if (slotItem == null || slotItem.getType() == Material.AIR) {
					if (amountRemaining > item.getMaxStackSize() && item.getMaxStackSize() != -1) {
						amountRemaining -= item.getMaxStackSize();
					} else {
						return true;
					}
				} else if (slotItem.isSimilar(item)) {
					int amountCanAdd = Math.min(amountRemaining, slotItem.getMaxStackSize() - slotItem.getAmount());
					amountRemaining -= amountCanAdd;
				}
			}
		}
		return amountRemaining <= 0;
	}

} 
