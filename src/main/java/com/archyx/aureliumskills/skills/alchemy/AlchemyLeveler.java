package com.archyx.aureliumskills.skills.alchemy;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.leveler.Leveler;
import com.archyx.aureliumskills.leveler.SkillLeveler;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class AlchemyLeveler extends SkillLeveler implements Listener {

	public AlchemyLeveler(AureliumSkills plugin) {
		super(plugin, Ability.BREWER);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBrew(BrewEvent event) {
		if (OptionL.isEnabled(Skills.ALCHEMY)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.ALCHEMY_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			if (event.getBlock().hasMetadata("skillsBrewingStandOwner")) {
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(event.getBlock().getMetadata("skillsBrewingStandOwner").get(0).asString()));
				if (offlinePlayer.isOnline()) {
					if (event.getContents().getIngredient() != null) {
						Player p = offlinePlayer.getPlayer();
						if (p != null) {
							if (blockXpGainLocation(event.getBlock().getLocation(), p)) return;
							if (blockXpGainPlayer(p)) return;
							Skill s = Skills.ALCHEMY;
							Material mat = event.getContents().getIngredient().getType();
							Leveler leveler = plugin.getLeveler();
							if (mat.equals(Material.REDSTONE)) {
								leveler.addXp(p, s, getXp(p, AlchemySource.EXTENDED));
							} else if (mat.equals(Material.GLOWSTONE_DUST)) {
								leveler.addXp(p, s, getXp(p, AlchemySource.UPGRADED));
							} else if (mat.equals(XMaterial.NETHER_WART.parseMaterial())) {
								leveler.addXp(p, s, getXp(p, AlchemySource.AWKWARD));
							} else if (mat.equals(XMaterial.GUNPOWDER.parseMaterial())) {
								leveler.addXp(p, s, getXp(p, AlchemySource.SPLASH));
							} else if (mat.equals(XMaterial.DRAGON_BREATH.parseMaterial())) {
								leveler.addXp(p, s, getXp(p, AlchemySource.LINGERING));
							} else {
								leveler.addXp(p, s, getXp(p, AlchemySource.REGULAR));
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType().equals(Material.BREWING_STAND)) {
			if (OptionL.isEnabled(Skills.ALCHEMY)) {
				event.getBlock().setMetadata("skillsBrewingStandOwner", new FixedMetadataValue(plugin, event.getPlayer().getUniqueId()));
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType().equals(Material.BREWING_STAND)) {
			if (OptionL.isEnabled(Skills.ALCHEMY)) {
				if (event.getBlock().hasMetadata("skillsBrewingStandOwner")) {
					event.getBlock().removeMetadata("skillsBrewingStandOwner", plugin);
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory().getType().equals(InventoryType.BREWING)) {
			if (OptionL.isEnabled(Skills.ALCHEMY)) {
				if (event.getInventory().getHolder() != null) {
					if (event.getInventory().getLocation() != null) {
						Block block = event.getInventory().getLocation().getBlock();
						if (!block.hasMetadata("skillsBrewingStandOwner")) {
							block.setMetadata("skillsBrewingStandOwner", new FixedMetadataValue(plugin, event.getPlayer().getUniqueId()));
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTakePotionOut(InventoryClickEvent event) {
		if (!OptionL.isEnabled(Skills.ALCHEMY)) return;
		// Check cancelled
		if (OptionL.getBoolean(Option.ALCHEMY_CHECK_CANCELLED)) {
			if (event.isCancelled()) {
				return;
			}
		}
		Inventory inventory = event.getClickedInventory();
		if (inventory == null) return;
		if (inventory.getType() != InventoryType.BREWING) return;

		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		if (event.getSlot() > 2) return; // Slots 0-2 are result slots
		InventoryAction action = event.getAction();
		// Filter out other actions
		if (action != InventoryAction.PICKUP_ALL && action != InventoryAction.PICKUP_HALF && action != InventoryAction.PICKUP_SOME
				&& action != InventoryAction.PICKUP_ONE && action != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			return;
		}
		ItemStack item = event.getCurrentItem();
		if (item == null) return;
		// player.sendMessage("You took out a potion");
	}
}
