package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.util.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Luck implements Listener {

	private final Random r = new Random();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		setLuck(event.getPlayer());
	}
	
	public static void reload(Player player) {
		if (player != null) {
			setLuck(player);
		}
	}

	@EventHandler
	public void worldChange(PlayerChangedWorldEvent event) {
		setLuck(event.getPlayer());
	}

	private static void setLuck(Player player) {
		AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_LUCK);
		if (attribute != null) {
			boolean hasModifier = false;
			//Removes existing modifiers of the same name
			for (AttributeModifier am : attribute.getModifiers()) {
				if (am.getName().equals("AureliumSkills-Luck")) {
					attribute.removeModifier(am);
					hasModifier = true;
				}
			}
			if (!hasModifier) {
				attribute.setBaseValue(0.0);
			}
			if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
				return;
			}
			if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
				double luck = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.LUCK) * Options.getDoubleOption(Setting.LUCK_MODIFIER);
				attribute.addModifier(new AttributeModifier("AureliumSkills-Luck", luck, AttributeModifier.Operation.ADD_NUMBER));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (Options.luckDoubleDrop) {
			//Checks if in disabled world
			if (AureliumSkills.worldManager.isInDisabledWorld(event.getBlock().getLocation())) {
				return;
			}
			//Checks if in blocked world
			if (AureliumSkills.worldManager.isInBlockedWorld(event.getBlock().getLocation())) {
				return;
			}
			//Checks if in blocked region
			if (AureliumSkills.worldGuardEnabled) {
				if (AureliumSkills.worldGuardSupport.isInBlockedRegion(event.getBlock().getLocation())) {
					return;
				}
			}
			if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
				if (event.getBlock().getType().isSolid()) {
					if (!event.isCancelled()) {
						if (!event.getBlock().hasMetadata("skillsPlaced")) {
							if (SkillLoader.playerStats.containsKey(event.getPlayer().getUniqueId())) {
								PlayerStat stat = SkillLoader.playerStats.get(event.getPlayer().getUniqueId());
								Material mat = event.getBlock().getType();
								if (mat.equals(Material.STONE) || mat.equals(Material.COBBLESTONE) || mat.equals(Material.SAND) || mat.equals(Material.GRAVEL)
										|| mat.equals(Material.DIRT) || mat.equals(XMaterial.GRASS_BLOCK.parseMaterial()) || mat.equals(XMaterial.ANDESITE.parseMaterial()) || mat.equals(XMaterial.DIORITE.parseMaterial())
										|| mat.equals(XMaterial.GRANITE.parseMaterial())) {
									if ((double) stat.getStatLevel(Stat.LUCK) * Options.getDoubleOption(Setting.DOUBLE_DROP_MODIFIER) < Options.getDoubleOption(Setting.DOUBLE_DROP_PERCENT_MAX) / 100) {
										if (r.nextDouble() < ((double) stat.getStatLevel(Stat.LUCK) * Options.getDoubleOption(Setting.DOUBLE_DROP_MODIFIER))) {
											for (ItemStack item : event.getBlock().getDrops()) {
												World world = event.getBlock().getLocation().getWorld();
												if (world != null) {
													world.dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), item);
												}
											}
										}
									} else {
										if (r.nextDouble() < Options.getDoubleOption(Setting.DOUBLE_DROP_PERCENT_MAX) / 100) {
											for (ItemStack item : event.getBlock().getDrops()) {
												World world = event.getBlock().getLocation().getWorld();
												if (world != null) {
													world.dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), item);
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
	
}
