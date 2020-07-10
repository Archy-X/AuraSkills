package io.github.archy_x.aureliumskills.menu;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

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
import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Lang;
import io.github.archy_x.aureliumskills.Message;
import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.skills.levelers.Leveler;
import io.github.archy_x.aureliumskills.skills.skilltree.SkillTree;
import io.github.archy_x.aureliumskills.util.RomanNumber;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class LevelProgressionMenu implements InventoryProvider {

	private Skill skill;
	private Player player;
	private List<Integer> track;
	
	public LevelProgressionMenu(Player player, Skill skill) {
		this.player = player;
		this.skill = skill;
		track = new LinkedList<Integer>();
		track.add(0); track.add(9); track.add(18); track.add(27); track.add(28); 
		track.add(29); track.add(20); track.add(11); track.add(2); track.add(3);
		track.add(4); track.add(13); track.add(22); track.add(31); track.add(32);
		track.add(33); track.add(24); track.add(15); track.add(6); track.add(7);
		track.add(8); track.add(17); track.add(26); track.add(35);
		track.add(36); track.add(45); track.add(54); track.add(63); track.add(64);
		track.add(65); track.add(56); track.add(47); track.add(38); track.add(39);
		track.add(40); track.add(49); track.add(58); track.add(67); track.add(68);
		track.add(69); track.add(60); track.add(51); track.add(42); track.add(43);
		track.add(44); track.add(53); track.add(62); track.add(71); 
		track.add(72); track.add(81); track.add(90); track.add(99); track.add(100);
		track.add(101); track.add(92); track.add(83); track.add(74); track.add(75);
		track.add(76); track.add(85); track.add(94); track.add(103); track.add(104);
		track.add(105); track.add(96); track.add(87); track.add(78); track.add(79);
		track.add(80); track.add(89); track.add(98); track.add(107);
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		int currentLevel = SkillLoader.playerSkills.get(this.player.getUniqueId()).getSkillLevel(skill);
		
		contents.set(SlotPos.of(0, 0), ClickableItem.empty(skill.getMenuItem(player, false)));
		
		if (Options.getBooleanOption(Setting.ENABLE_SKILL_POINTS)) {
			if (skill.equals(Skill.FARMING)) {
				contents.set(SlotPos.of(0, 1), ClickableItem.of(getSkillTreeItem(), e -> {
					SkillTreeMenu.getInventory(SkillTree.valueOf(skill.toString()), true).open(player);
				}));
			}
		}
		
		contents.set(SlotPos.of(5, 0), ClickableItem.of(MenuItems.getBackButton(Lang.getMessage(Message.BACK_SKILLS_MENU)), e -> {
			SkillsMenu.getInventory(player).open(player);
		}));
		
		contents.set(SlotPos.of(5, 1), ClickableItem.of(MenuItems.getCloseButton(), e -> {
			player.closeInventory();
		}));
		
		Pagination pagination = contents.pagination();
		ClickableItem[] items = new ClickableItem[108];
		
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
		
		contents.set(5, 8, ClickableItem.of(getNextPageItem(), e -> {
			int page = pagination.next().getPage();
			getInventory(player, skill, page).open(player, page);
		}));

		contents.set(5, 7, ClickableItem.of(getPreviousPageItem(), e -> {
			int previous = pagination.previous().getPage();
			getInventory(player, skill, previous).open(player, previous);
		}));
		
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
	
	public ItemStack getSkillTreeItem() {
		ItemStack item = XMaterial.JUNGLE_SAPLING.parseItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + Lang.getMessage(Message.SKILL_TREE_NAME));
		List<String> lore = new LinkedList<String>();
		String fullDesc = Lang.getMessage(Message.SKILL_TREE_DESCRIPTION);
		String[] splitDesc = fullDesc.replaceAll("(?:\\s*)(.{1,"+ 38 +"})(?:\\s+|\\s*$)", "$1\n").split("\n");
		for (String s : splitDesc) {
			lore.add(ChatColor.GRAY + s);
		}
		lore.add(" ");
		lore.add(ChatColor.YELLOW + Lang.getMessage(Message.SKILL_TREE_CLICK));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
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
		if (Options.getBooleanOption(Setting.ENABLE_SKILL_POINTS)) {
			if (Leveler.skillPointRewards.get(level - 2) == 1) {
				lore.add(ChatColor.AQUA + "   +" + Leveler.skillPointRewards.get(level - 2) + " " + Lang.getMessage(Message.SKILL_POINTS_SINGULAR));
			}
			else {
				lore.add(ChatColor.AQUA + "   +" + Leveler.skillPointRewards.get(level - 2) + " " + Lang.getMessage(Message.SKILL_POINTS_PLURAL));
			}
		}
		lore.add(" ");
		return lore;
	}
	
}
