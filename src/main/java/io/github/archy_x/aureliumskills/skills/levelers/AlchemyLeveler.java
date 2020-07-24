package io.github.archy_x.aureliumskills.skills.levelers;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.Source;
import io.github.archy_x.aureliumskills.util.XMaterial;
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
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class AlchemyLeveler implements Listener {

	private Plugin plugin;
	
	public AlchemyLeveler(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBrew(BrewEvent event) {
		if (Options.isEnabled(Skill.ALCHEMY)) {
			if (event.isCancelled() == false) {
				//Checks if in blocked region
				if (AureliumSkills.worldGuardEnabled) {
					if (AureliumSkills.worldGuardSupport.isInBlockedRegion(event.getBlock().getLocation())) {
						return;
					}
				}
				if (event.getBlock().hasMetadata("skillsBrewingStandOwner")) {
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(event.getBlock().getMetadata("skillsBrewingStandOwner").get(0).asString()));
					if (offlinePlayer.isOnline()) {
						if (event.getContents().getIngredient() != null) {
							Player p = offlinePlayer.getPlayer();
							Skill s = Skill.ALCHEMY;
							Material mat = event.getContents().getIngredient().getType();
							if (mat.equals(Material.REDSTONE)) {
								Leveler.addXp(p, s, Source.EXTENDED);
							}
							else if (mat.equals(Material.GLOWSTONE_DUST)) {
								Leveler.addXp(p, s, Source.UPGRADED);
							}
							else if (mat.equals(XMaterial.NETHER_WART.parseMaterial())) {
								Leveler.addXp(p, s, Source.AWKWARD);
							}
							else if (mat.equals(XMaterial.GUNPOWDER.parseMaterial())) {
								Leveler.addXp(p, s, Source.SPLASH);
							}
							else if (mat.equals(XMaterial.DRAGON_BREATH.parseMaterial())) {
								Leveler.addXp(p, s, Source.LINGERING);
							}
							else {
								Leveler.addXp(p, s, Source.REGULAR);
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
			if (Options.isEnabled(Skill.ALCHEMY)) {
				event.getBlock().setMetadata("skillsBrewingStandOwner", new FixedMetadataValue(plugin, event.getPlayer().getUniqueId()));
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType().equals(Material.BREWING_STAND)) {
			if (Options.isEnabled(Skill.ALCHEMY)) {
				if (event.getBlock().hasMetadata("skillsBrewingStandOwner")) {
					event.getBlock().removeMetadata("skillsBrewingStandOwner", plugin);
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory().getType().equals(InventoryType.BREWING)) {
			if (Options.isEnabled(Skill.ALCHEMY)) {
				if (event.getInventory().getHolder() != null) {
					Block block = event.getInventory().getLocation().getBlock();
					if (block.hasMetadata("skillsBrewingStandOwner") == false) {
						block.setMetadata("skillsBrewingStandOwner", new FixedMetadataValue(plugin, event.getPlayer().getUniqueId()));
					}
				}
			}
		}
	}
}
