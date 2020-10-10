package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.util.ItemUtils;
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
		
		contents.set(SlotPos.of(5, 0), ClickableItem.of(MenuItems.getBackButton(Lang.getMessage(MenuMessage.BACK_CLICK)), e -> SkillsMenu.getInventory(player).open(player)));
		
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
				.title(Lang.getMessage(MenuMessage.LEVEL_PROGRESSION_MENU_TITLE).replace("{skill}", skill.getDisplayName()).replace("{page}", String.valueOf(page + 1)))
				.manager(AureliumSkills.invManager)
				.build();
	}
	
	public ItemStack getNextPageItem() {
		ItemStack item = new ItemStack(Material.ARROW);
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(ChatColor.GOLD + Lang.getMessage(MenuMessage.NEXT_PAGE));
			List<String> lore = new LinkedList<>();
			lore.add(ChatColor.YELLOW + Lang.getMessage(MenuMessage.NEXT_PAGE_CLICK));
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}
	
	public ItemStack getPreviousPageItem() {
		ItemStack item = new ItemStack(Material.ARROW);
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(ChatColor.GOLD + Lang.getMessage(MenuMessage.PREVIOUS_PAGE));
			List<String> lore = new LinkedList<>();
			lore.add(ChatColor.YELLOW + Lang.getMessage(MenuMessage.PREVIOUS_PAGE_CLICK));
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}
	
	public ItemStack getUnlockedLevel(int level) {
		ItemStack item = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(Lang.getMessage(MenuMessage.LEVEL_UNLOCKED).replace("{level}", RomanNumber.toRoman(level)));
				List<String> lore = getLore(level);
				lore.add(Lang.getMessage(MenuMessage.UNLOCKED));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
		}
		return item;
	}
	
	public ItemStack getCurrentLevel(int level) {
		ItemStack item = XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem();
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(Lang.getMessage(MenuMessage.LEVEL_IN_PROGRESS).replace("{level}", RomanNumber.toRoman(level)));
				List<String> lore = getLore(level);
				double xp = SkillLoader.playerSkills.get(player.getUniqueId()).getXp(skill);
				double xpToNext;
				if (Leveler.levelReqs.size() > level - 2) {
					xpToNext = Leveler.levelReqs.get(level - 2);
				} else {
					xpToNext = 0;
				}
				NumberFormat nf = new DecimalFormat("##.##");
				lore.add(ChatColor.GRAY + "Progress: " + ChatColor.YELLOW + nf.format(xp / xpToNext * 100) + "%");
				lore.add(ChatColor.GRAY + "   " + nf.format(xp) + "/" + (int) xpToNext + " XP");
				lore.add(" ");
				lore.add(Lang.getMessage(MenuMessage.IN_PROGRESS));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
		}
		return item;
	}
	
	public ItemStack getLockedLevel(int level) {
		ItemStack item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(Lang.getMessage(MenuMessage.LEVEL_LOCKED).replace("{level}", RomanNumber.toRoman(level)));
				List<String> lore = getLore(level);
				lore.add(Lang.getMessage(MenuMessage.LOCKED));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
		}
		return item;
	}

	private ItemStack getRankItem() {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(Lang.getMessage(MenuMessage.YOUR_RANKING));
			List<String> lore = new ArrayList<>();
			int rank = AureliumSkills.leaderboard.getSkillRank(skill, player.getUniqueId());
			int total = AureliumSkills.leaderboard.getSize();
			lore.add(Lang.getMessage(MenuMessage.RANK_OUT_OF)
					.replace("{rank}", String.valueOf(rank))
					.replace("{total}", String.valueOf(total)));
			NumberFormat nf;
			double percentRank = (double) rank / (double) total * 100;
			if (percentRank < 1) {
				nf = new DecimalFormat("#.###");
			} else {
				nf = new DecimalFormat("#.#");
			}
			lore.add(Lang.getMessage(MenuMessage.RANK_PERCENT).replace("{percent}", nf.format(percentRank)));
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}

	public List<String> getLore(int level) {
		List<String> lore = new LinkedList<>();
		NumberFormat nf = new DecimalFormat("#.#");
		lore.add(Lang.getMessage(MenuMessage.LEVEL_NUMBER).replace("{level}", String.valueOf(level)));
		if (level%2 != 0) {
			lore.add(Lang.getMessage(MenuMessage.REWARDS_ONE)
					.replace("{color}",skill.getPrimaryStat().getColor())
					.replace("{num}", String.valueOf(1))
					.replace("{symbol}", skill.getPrimaryStat().getSymbol())
					.replace("{stat}", skill.getPrimaryStat().getDisplayName()));
		}
		else {
			lore.add(Lang.getMessage(MenuMessage.REWARDS_TWO)
					.replace("{color_1}",skill.getPrimaryStat().getColor())
					.replace("{num_1}", String.valueOf(1))
					.replace("{symbol_1}", skill.getPrimaryStat().getSymbol())
					.replace("{stat_1}", skill.getPrimaryStat().getDisplayName())
					.replace("{color_2}",skill.getSecondaryStat().getColor())
					.replace("{num_2}", String.valueOf(1))
					.replace("{symbol_2}", skill.getSecondaryStat().getSymbol())
					.replace("{stat_2}", skill.getSecondaryStat().getDisplayName()));
		}
		Ability ability = getAbility(level);
		if (ability != null) {
			if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
				lore.add(" ");
				if (level <= 6) {
					lore.add(Lang.getMessage(MenuMessage.ABILITY_UNLOCK)
							.replace("{ability}", ability.getDisplayName())
							.replace("{desc}", ability.getDescription())
							.replace("{value_2}", nf.format(ability.getValue2((level + 3) / 5)))
							.replace("{value}", nf.format(ability.getValue((level + 3) / 5))));
				}
				else {
					lore.add(Lang.getMessage(MenuMessage.ABILITY_LEVEL)
							.replace("{ability}", ability.getDisplayName())
							.replace("{level}", RomanNumber.toRoman((level + 3) / 5))
							.replace("{desc}", ability.getDescription())
							.replace("{value_2}", nf.format(ability.getValue2((level + 3) / 5)))
							.replace("{value}", nf.format(ability.getValue((level + 3) / 5))));
				}
			}
		}
		//Show mana ability unlock/level up
		if (level % 7 == 0) {
			MAbility mAbility = skill.getManaAbility();
			if (AureliumSkills.abilityOptionManager.isEnabled(mAbility)) {
				lore.add(" ");
				if (level == 7) {
					lore.add(Lang.getMessage(MenuMessage.MANA_ABILITY_UNLOCK)
							.replace("{mana_ability}", mAbility.getDisplayName())
							.replace("{desc}", mAbility.getDescription()
									.replace("{value}", String.valueOf(mAbility.getValue(level / 7)))));
				}
				else {
					lore.add(Lang.getMessage(MenuMessage.MANA_ABILITY_LEVEL)
							.replace("{mana_ability}", mAbility.getDisplayName())
							.replace("{level}", RomanNumber.toRoman(level / 7))
							.replace("{desc}", mAbility.getDescription()
									.replace("{value}", String.valueOf(mAbility.getValue(level / 7)))));
				}
			}
		}
		lore.add(" ");
		return ItemUtils.formatLore(lore);
	}
	
	public Ability getAbility(int level) {
		if (skill.getAbilities().size() == 5) {
			if (level % 5 == 2) {
				return skill.getAbilities().get(0).get();
			}
			else if (level % 5 == 3) {
				return skill.getAbilities().get(1).get();
			}
			else if (level % 5 == 4) {
				return skill.getAbilities().get(2).get();
			}
			else if (level % 5 == 0) {
				return skill.getAbilities().get(3).get();
			}
			else if (level % 5 == 1) {
				return skill.getAbilities().get(4).get();
			}
		}
		return null;
	}
	
}
