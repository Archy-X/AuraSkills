package io.github.archy_x.aureliumskills.stats;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class Luck implements Listener {

	private Random r = new Random();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (SkillLoader.playerStats.containsKey(event.getPlayer().getUniqueId())) {
			double luck = SkillLoader.playerStats.get(event.getPlayer().getUniqueId()).getStatLevel(Stat.LUCK) * Options.getDoubleOption(Setting.LUCK_MODIFIER);
			event.getPlayer().getAttribute(Attribute.GENERIC_LUCK).setBaseValue(luck);
		}
	}
	
	public static void reload(Player player) {
		if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
			double luck = SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.LUCK) * Options.getDoubleOption(Setting.LUCK_MODIFIER);
			player.getAttribute(Attribute.GENERIC_LUCK).setBaseValue(luck);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
			if (event.getBlock().getType().isSolid()) {
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
										event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), item);
									}
								}
							}
							else {
								if (r.nextDouble() < Options.getDoubleOption(Setting.DOUBLE_DROP_PERCENT_MAX) / 100) {
									for (ItemStack item : event.getBlock().getDrops()) {
										event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), item);
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
