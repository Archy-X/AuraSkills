package io.github.archy_x.aureliumskills.skills.levelers;

import java.util.UUID;

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

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class AlchemyLeveler implements Listener {

	private Plugin plugin;
	
	public AlchemyLeveler(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBrew(BrewEvent event) {
		if (event.isCancelled() == false) {
			if (event.getBlock().hasMetadata("skillsBrewingStandOwner")) {
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(event.getBlock().getMetadata("skillsBrewingStandOwner").get(0).asString()));
				if (offlinePlayer.isOnline()) {
					if (event.getContents().getIngredient() != null) {
						Player player = offlinePlayer.getPlayer();
						Material mat = event.getContents().getIngredient().getType();
						if (mat.equals(Material.REDSTONE) || mat.equals(Material.GLOWSTONE_DUST)) {
							if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
								SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ALCHEMY, 25);
								Leveler.playSound(player);
								Leveler.checkLevelUp(player, Skill.ALCHEMY);
								Leveler.sendActionBarMessage(player, Skill.ALCHEMY, 25);
							}
						}
						else if (mat.equals(XMaterial.NETHER_WART.parseMaterial())) {
							if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
								SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ALCHEMY, 10);
								Leveler.playSound(player);
								Leveler.checkLevelUp(player, Skill.ALCHEMY);
								Leveler.sendActionBarMessage(player, Skill.ALCHEMY, 10);
							}
						}
						else if (mat.equals(XMaterial.GUNPOWDER.parseMaterial())) {
							if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
								SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ALCHEMY, 35);
								Leveler.playSound(player);
								Leveler.checkLevelUp(player, Skill.ALCHEMY);
								Leveler.sendActionBarMessage(player, Skill.ALCHEMY, 35);
							}
						}
						else if (mat.equals(XMaterial.DRAGON_BREATH.parseMaterial())) {
							if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
								SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ALCHEMY, 50);
								Leveler.playSound(player);
								Leveler.checkLevelUp(player, Skill.ALCHEMY);
								Leveler.sendActionBarMessage(player, Skill.ALCHEMY, 50);
							}
						}
						else {
							if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
								SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ALCHEMY, 15);
								Leveler.playSound(player);
								Leveler.checkLevelUp(player, Skill.ALCHEMY);
								Leveler.sendActionBarMessage(player, Skill.ALCHEMY, 15);
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
			event.getBlock().setMetadata("skillsBrewingStandOwner", new FixedMetadataValue(plugin, event.getPlayer().getUniqueId()));
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType().equals(Material.BREWING_STAND)) {
			if (event.getBlock().hasMetadata("skillsBrewingStandOwner")) {
				event.getBlock().removeMetadata("skillsBrewingStandOwner", plugin);
			}
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory().getType().equals(InventoryType.BREWING)) {
			if (event.getInventory().getHolder() != null) {
				Block block = event.getInventory().getLocation().getBlock();
				if (block.hasMetadata("skillsBrewingStandOwner") == false) {
					block.setMetadata("skillsBrewingStandOwner", new FixedMetadataValue(plugin, event.getPlayer().getUniqueId()));
				}
			}
		}
	}
}
