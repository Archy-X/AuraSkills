package io.github.archy_x.aureliumskills.menu;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.dbassett.skullcreator.SkullCreator;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.stats.PlayerStat;
import io.github.archy_x.aureliumskills.stats.Stat;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class StatsMenu implements InventoryProvider{

	private Player player;
	
	public StatsMenu(Player player) {
		this.player = player;
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fill(ClickableItem.empty(MenuItems.getEmptyPane()));
		contents.set(SlotPos.of(1, 4), ClickableItem.empty(getPlayerHead(SkillLoader.playerStats.get(player.getUniqueId()))));
		contents.set(SlotPos.of(1, 1), ClickableItem.empty(getStatItem(
			ChatColor.DARK_RED + "Strength", 14, "Strength increases your attack damage", "with various different weapons", 
			"Foraging, Fighting, Sorcery", "Farming, Archery")));
		contents.set(SlotPos.of(1, 2), ClickableItem.empty(getStatItem(
			ChatColor.RED + "Health", 1, "Health increases the amount of HP you", "have, allowing you to last longer in fights", 
			"Farming, Alchemy", "Fishing, Defense, Healing")));
		contents.set(SlotPos.of(1, 3), ClickableItem.empty(getStatItem(
			ChatColor.GOLD + "Regeneration", 4, "Regeneration increases how fast you", "recover both health and mana", 
			"Excavation, Endurance, Healing", "Fighting, Agility")));
		contents.set(SlotPos.of(1, 5), ClickableItem.empty(getStatItem(
			ChatColor.DARK_GREEN + "Luck", 13, "Luck increases your chances of getting", "rare loot from mobs, fishing, and more", 
			"Fishing, Archery", "Mining, Excavation, Enchanting")));
		contents.set(SlotPos.of(1, 6), ClickableItem.empty(getStatItem(
			ChatColor.BLUE + "Wisdom", 11, "Wisdom increases your mana pool, the", "strength of magical attacks, and magical luck", 
			"Agility, Enchanting", "Alchemy, Sorcery, Forging")));
		contents.set(SlotPos.of(1, 7), ClickableItem.empty(getStatItem(
			ChatColor.DARK_PURPLE + "Toughness", 10, "Toughness increases the amount of damage", "reduced from enemy attacks", 
			"Mining, Defense, Forging", "Foraging, Endurance")));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
		
		
	}

	private ItemStack getStatItem(String name, int color, String desc1, String desc2, String primarySkills, String secondarySkills) {
		ItemStack item = XMaterial.WHITE_STAINED_GLASS_PANE.parseItem();
		if (color == 14) {
			item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 1) {
			item = XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 4) {
			item = XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 13) {
			item = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 11) {
			item = XMaterial.BLUE_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 10) {
			item = XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem();
		}
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		List<String> lore = new LinkedList<String>();
		lore.add(ChatColor.GRAY + desc1);
		lore.add(ChatColor.GRAY + desc2);
		lore.add(" ");
		lore.add(ChatColor.GRAY + "Primary Skills: " + ChatColor.RESET + primarySkills);
		lore.add(ChatColor.GRAY + "Secondary Skills: " + ChatColor.RESET + secondarySkills);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack getPlayerHead(PlayerStat stat) {
		ItemStack item = SkullCreator.itemFromUuid(player.getUniqueId());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + player.getName());
		List<String> lore = new LinkedList<String>();
		lore.add(ChatColor.DARK_RED + "  ➽ Strength " + ChatColor.WHITE + stat.getStatLevel(Stat.STRENGTH));
		lore.add(ChatColor.RED + "  ❤ Health " + ChatColor.WHITE + stat.getStatLevel(Stat.HEALTH));
		lore.add(ChatColor.GOLD + "  ❥ Regeneration " + ChatColor.WHITE + stat.getStatLevel(Stat.REGENERATION));
		lore.add(ChatColor.DARK_GREEN + "  ☘ Luck " + ChatColor.WHITE + stat.getStatLevel(Stat.LUCK));
		lore.add(ChatColor.BLUE + "  ✿ Wisdom " + ChatColor.WHITE + stat.getStatLevel(Stat.WISDOM));
		lore.add(ChatColor.DARK_PURPLE + "  ✦ Toughness " + ChatColor.WHITE + stat.getStatLevel(Stat.TOUGHNESS));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static SmartInventory getInventory(Player player) {
		return SmartInventory.builder()
				.provider(new StatsMenu(player))
				.size(3, 9)
				.title("Your Stats")
				.manager(AureliumSkills.invManager)
				.build();
	}
	
}
