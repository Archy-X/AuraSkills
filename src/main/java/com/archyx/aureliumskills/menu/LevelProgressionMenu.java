package com.archyx.aureliumskills.menu;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.util.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotPos;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.util.RomanNumber;

public class LevelProgressionMenu implements InventoryProvider {

	private Skill skill;
	private Player player;
	private List<Integer> track;
	
	public final static int pages = 4;
	
	public LevelProgressionMenu(Player player, Skill skill) {
		this.player = player;
		this.skill = skill;
		track = new LinkedList<Integer>();
		for (int i = 0; i < pages; i++) {
			track.add(i*36); track.add(9 + i*36); track.add(18 + i*36); track.add(27 + i*36); track.add(28 + i*36);
			track.add(29 + i*36); track.add(20 + i*36); track.add(11 + i*36); track.add(2 + i*36); track.add(3 + i*36);
			track.add(4 + i*36); track.add(13 + i*36); track.add(22 + i*36); track.add(31 + i*36); track.add(32 + i*36);
			track.add(33 + i*36); track.add(24 + i*36); track.add(15 + i*36); track.add(6 + i*36); track.add(7 + i*36);
			track.add(8 + i*36); track.add(17 + i*36); track.add(26 + i*36); track.add(35 + i*36);
		}
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		int currentLevel = SkillLoader.playerSkills.get(this.player.getUniqueId()).getSkillLevel(skill);
		
		contents.set(SlotPos.of(0, 0), ClickableItem.empty(skill.getMenuItem(player, false)));
		
		contents.set(SlotPos.of(5, 0), ClickableItem.of(MenuItems.getBackButton(Lang.getMessage(Message.BACK_SKILLS_MENU)), e -> {
			SkillsMenu.getInventory(player).open(player);
		}));
		
		contents.set(SlotPos.of(5, 1), ClickableItem.of(MenuItems.getCloseButton(), e -> {
			player.closeInventory();
		}));
		
		Pagination pagination = contents.pagination();
		ClickableItem[] items = new ClickableItem[pages * 36];
		
		for (int i = 0; i < track.size(); i++) {
			if (i + 2 <= currentLevel) {
				items[track.get(i)] = ClickableItem.empty(getUnlockedLevel(i+2));
			}
			else if (i + 2 == currentLevel + 1) {
				items[track.get(i)] = ClickableItem.empty(getCurrentLevel(i+2));
			}
			else {
				items[track.get(i)] = ClickableItem.empty(getLockedLevel(i+2));
			}
			
		}
		
		pagination.setItems(items);
		pagination.setItemsPerPage(36);
		
		int a = 0;
		for (int i = 9; i < 45; i++) {
			int row = i/9;
			int column = i%9;
			contents.set(row, column, pagination.getPageItems()[a]);
			a++;
		}
		
		if (pagination.getPage() + 1 < pages) {
			contents.set(5, 8, ClickableItem.of(getNextPageItem(), e -> {
				int page = pagination.next().getPage();
				getInventory(player, skill, page).open(player, page);
			}));
		}

		if (pagination.getPage() - 1 >= 0) {
			contents.set(5, 7, ClickableItem.of(getPreviousPageItem(), e -> {
				int previous = pagination.previous().getPage();
				getInventory(player, skill, previous).open(player, previous);
			}));
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {
		
		
	}

	public static SmartInventory getInventory(Player player, Skill skill, int page) {
		return SmartInventory.builder()
				.provider(new LevelProgressionMenu(player, skill))
				.size(6, 9)
				.title(Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.LEVELS) + " - " + Lang.getMessage(Message.PAGE).replace("_", String.valueOf(page + 1)))
				.manager(AureliumSkills.invManager)
				.build();
	}
	
	public ItemStack getNextPageItem() {
		ItemStack item = new ItemStack(Material.ARROW);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + Lang.getMessage(Message.NEXT_PAGE));
		List<String> lore = new LinkedList<String>();
		lore.add(ChatColor.YELLOW + Lang.getMessage(Message.NEXT_PAGE_CLICK));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack getPreviousPageItem() {
		ItemStack item = new ItemStack(Material.ARROW);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + Lang.getMessage(Message.PREVIOUS_PAGE));
		List<String> lore = new LinkedList<String>();
		lore.add(ChatColor.YELLOW + Lang.getMessage(Message.PREVIOUS_PAGE_CLICK));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack getUnlockedLevel(int level) {
		ItemStack item = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + Lang.getMessage(Message.LEVEL) + " " + RomanNumber.toRoman(level));
		List<String> lore = getLore(level);
		lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + Lang.getMessage(Message.UNLOCKED));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack getCurrentLevel(int level) {
		ItemStack item = XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + Lang.getMessage(Message.LEVEL) + " " + RomanNumber.toRoman(level));
		List<String> lore = getLore(level);
		double xp = SkillLoader.playerSkills.get(player.getUniqueId()).getXp(skill);
		double xpToNext;
		if (Leveler.levelReqs.size() > level - 1) {
			xpToNext = Leveler.levelReqs.get(level - 1);
		}
		else {
			xpToNext = 0;
		}
		NumberFormat nf = new DecimalFormat("##.##");
		lore.add(ChatColor.GRAY + "Progress: " + ChatColor.YELLOW + nf.format(xp/xpToNext * 100) + "%");
		lore.add(ChatColor.GRAY + "   " + nf.format(xp) + "/" + (int) xpToNext + " XP");
		lore.add(" ");
		lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + Lang.getMessage(Message.IN_PROGRESS));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack getLockedLevel(int level) {
		ItemStack item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + Lang.getMessage(Message.LEVEL) + " " + RomanNumber.toRoman(level));
		List<String> lore = getLore(level);
		lore.add(ChatColor.RED + "" + ChatColor.BOLD + Lang.getMessage(Message.LOCKED));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public List<String> getLore(int level) {
		List<String> lore = new LinkedList<String>();
		lore.add(ChatColor.GRAY + Lang.getMessage(Message.LEVEL) + " " + ChatColor.WHITE + level);
		lore.add(ChatColor.GRAY + Lang.getMessage(Message.REWARDS) + ":");
		lore.add(skill.getPrimaryStat().getColor() + "   +1 " + skill.getPrimaryStat().getSymbol() + " " + Lang.getMessage(Message.valueOf(skill.getPrimaryStat().toString().toUpperCase() + "_NAME")));
		if (level%2 == 0) {
			lore.add(skill.getSecondaryStat().getColor() + "   +1 " + skill.getSecondaryStat().getSymbol() + " " + Lang.getMessage(Message.valueOf(skill.getSecondaryStat().toString().toUpperCase() + "_NAME")));
		}
		if (getAbility(level) != null) {
			Ability ability = getAbility(level);
			if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
				lore.add(" ");
				if (level <= 6) {
					lore.add(ChatColor.GOLD + ability.getDisplayName() + ChatColor.GREEN + "" + ChatColor.BOLD + " Ability Unlock");
				} else {
					lore.add(ChatColor.GOLD + ability.getDisplayName() + " " + RomanNumber.toRoman((level + 3) / 5));
				}
				NumberFormat nf = new DecimalFormat("##.##");
				String fullDesc = ability.getDescription().replace("_", nf.format(ability.getValue((level + 3) / 5)));
				String[] splitDesc = fullDesc.replaceAll("(?:\\s*)(.{1," + 35 + "})(?:\\s+|\\s*$)", "$1\n").split("\n");
				for (String s : splitDesc) {
					lore.add(ChatColor.GRAY + "   " + s);
				}
			}
		}
		lore.add(" ");
		return lore;
	}
	
	public Ability getAbility(int level) {
		if (skill.getAbilities().length == 5) {
			if (level % 5 == 2) {
				return skill.getAbilities()[0];
			}
			else if (level % 5 == 3) {
				return skill.getAbilities()[1];
			}
			else if (level % 5 == 4) {
				return skill.getAbilities()[2];
			}
			else if (level % 5 == 0) {
				return skill.getAbilities()[3];
			}
			else if (level % 5 == 1) {
				return skill.getAbilities()[4];
			}
		}
		return null;
		
	}
	
}
