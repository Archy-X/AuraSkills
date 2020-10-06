package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.util.RomanNumber;
import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LevelProgressionMenu implements InventoryProvider {

	private final Skill skill;
	private final Player player;
	private final List<Integer> track;
	
	private int pages = 4;
	
	public LevelProgressionMenu(Player player, Skill skill) {
		this.player = player;
		this.skill = skill;
		int maxLevel = OptionL.getMaxLevel(skill);
		if (maxLevel < 26) {
			pages = 1;
		}
		else if (maxLevel < 50) {
			pages = 2;
		}
		else if (maxLevel < 74) {
			pages = 3;
		}
		track = new LinkedList<>();
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
		PlayerSkill playerSkill = SkillLoader.playerSkills.get(this.player.getUniqueId());
		int currentLevel;
		if (playerSkill != null) {
			 currentLevel = playerSkill.getSkillLevel(skill);
		}
		else {
			currentLevel = 1;
		}
		if (OptionL.getBoolean(Option.LEVEL_PROGRESSION_MENU_FILL_PANE)) {
			contents.fill(ClickableItem.empty(MenuItems.getEmptyPane()));
		}

		contents.set(SlotPos.of(0, 0), ClickableItem.empty(skill.getMenuItem(player, false)));
		
		contents.set(SlotPos.of(5, 0), ClickableItem.of(MenuItems.getBackButton(Lang.getMessage(Message.BACK_SKILLS_MENU)), e -> SkillsMenu.getInventory(player).open(player)));
		
		contents.set(SlotPos.of(5, 1), ClickableItem.of(MenuItems.getCloseButton(), e -> player.closeInventory()));

		contents.set(SlotPos.of(0, 1), ClickableItem.empty(getRankItem()));
		
		Pagination pagination = contents.pagination();
		ClickableItem[] items = new ClickableItem[pages * 36];

		if (OptionL.getBoolean(Option.LEVEL_PROGRESSION_MENU_FILL_PANE)) {
			for (int i = 0; i < items.length; i++) {
				items[i] = ClickableItem.empty(MenuItems.getEmptyPane());
			}
		}
		
		for (int i = 0; i < track.size(); i++) {
			if (i + 2 <= OptionL.getMaxLevel(skill)) {
				if (i + 2 <= currentLevel) {
					items[track.get(i)] = ClickableItem.empty(getUnlockedLevel(i + 2));
				} else if (i + 2 == currentLevel + 1) {
					items[track.get(i)] = ClickableItem.empty(getCurrentLevel(i + 2));
				} else {
					items[track.get(i)] = ClickableItem.empty(getLockedLevel(i + 2));
				}
			}
			else {
				if (OptionL.getBoolean(Option.LEVEL_PROGRESSION_MENU_FILL_PANE)) {
					items[track.get(i)] = ClickableItem.empty(MenuItems.getEmptyPane());
				}
				else {
					items[track.get(i)] = ClickableItem.empty(new ItemStack(Material.AIR));
				}
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
		if (Leveler.levelReqs.size() > level - 2) {
			xpToNext = Leveler.levelReqs.get(level - 2);
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

	private ItemStack getRankItem() {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Lang.getMessage(Message.MENU_RANK_NAME).replace("&", "§"));
		List<String> lore = new ArrayList<>();
		int rank = AureliumSkills.leaderboard.getSkillRank(skill, player.getUniqueId());
		int total =  AureliumSkills.leaderboard.getSize();
		lore.add(Lang.getMessage(Message.MENU_RANK_RANKED).replace("&", "§").replace("$rank$", String.valueOf(rank)).replace("$total$", String.valueOf(total)));
		NumberFormat nf;
		double percentRank = (double) rank / (double) total * 100;
		if (percentRank < 1) {
			nf = new DecimalFormat("#.###");
		}
		else {
			nf = new DecimalFormat("#.#");
		}
		lore.add(Lang.getMessage(Message.MENU_RANK_PERCENT).replace("&", "§").replace("$percent$", nf.format(percentRank)));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public List<String> getLore(int level) {
		List<String> lore = new LinkedList<>();
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
					lore.add(ChatColor.GOLD + ability.getDisplayName() + ChatColor.GREEN + "" + ChatColor.BOLD + " " + Lang.getMessage(Message.ABILITY_UNLOCK));
				} else {
					lore.add(ChatColor.GOLD + ability.getDisplayName() + " " + RomanNumber.toRoman((level + 3) / 5));
				}
				NumberFormat nf = new DecimalFormat("##.##");
				String fullDesc = ability.getDescription().replace("_", nf.format(ability.getValue((level + 3) / 5)));
				if (ability.hasTwoValues()) {
					fullDesc = fullDesc.replace("$", nf.format(ability.getValue2((level + 3) / 5)));
				}
				String[] splitDesc = fullDesc.replaceAll("(?:\\s*)(.{1," + 35 + "})(?:\\s+|\\s*$)", "$1\n").split("\n");
				for (String s : splitDesc) {
					lore.add(ChatColor.GRAY + "   " + s);
				}
			}
		}
		//Show mana ability unlock/level up
		if (level % 7 == 0) {
			MAbility mAbility = skill.getManaAbility();
			if (AureliumSkills.abilityOptionManager.isEnabled(mAbility)) {
				lore.add(" ");
				if (level == 7) {
					lore.add(ChatColor.BLUE + mAbility.getName() + ChatColor.LIGHT_PURPLE + " " + ChatColor.BOLD + Lang.getMessage(Message.MANA_ABILITY_UNLOCK));
				}
				else {
					lore.add(ChatColor.BLUE + mAbility.getName() + " " + RomanNumber.toRoman(level / 7));
				}
				NumberFormat nf = new DecimalFormat("##.##");
				String fullDesc = mAbility.getDescription().replace("_", nf.format(mAbility.getValue(level / 7)));
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
