package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
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
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class LevelProgressionMenu implements InventoryProvider {

	private final Skill skill;
	private final Player player;
	private final List<Integer> track;
	private final MenuOption options;

	private int pages = 4;
	
	public LevelProgressionMenu(Player player, Skill skill) {
		this.player = player;
		this.skill = skill;
		options = AureliumSkills.getMenuLoader().getMenu("level_progression_menu");
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
		long start = System.nanoTime();
		PlayerSkill playerSkill = SkillLoader.playerSkills.get(this.player.getUniqueId());
		int currentLevel = playerSkill.getSkillLevel(skill);

		// Fill item
		if (options.isFillEnabled()) {
			contents.fill(ClickableItem.empty(options.getFillItem()));
		}

		ItemOption skillItem = options.getItem("skill");
		contents.set(SlotPos.of(skillItem.getRow(), skillItem.getColumn()), ClickableItem.empty(getSkillItem(skillItem, skill)));

		ItemOption backButton = options.getItem("back");
		contents.set(SlotPos.of(backButton.getRow(), backButton.getColumn()), ClickableItem.of(getBackItem(backButton), e -> SkillsMenu.getInventory(player).open(player)));

		ItemOption closeButton = options.getItem("close");
		contents.set(SlotPos.of(closeButton.getRow(), closeButton.getColumn()), ClickableItem.of(getCloseItem(closeButton), e -> player.closeInventory()));

		ItemOption rankItem = options.getItem("rank");
		contents.set(SlotPos.of(0, 1), ClickableItem.empty(getRankItem(rankItem)));
		
		Pagination pagination = contents.pagination();
		ClickableItem[] items = new ClickableItem[pages * 36];

		if (options.isFillEnabled() && options.getFillItem() != null) {
			for (int i = 0; i < items.length; i++) {
				items[i] = ClickableItem.empty(options.getFillItem());
			}
		}

		ItemTemplate unlocked = options.getTemplate("unlocked");
		ItemTemplate inProgress = options.getTemplate("in_progress");
		ItemTemplate locked = options.getTemplate("locked");

		for (int i = 0; i < track.size(); i++) {
			if (i + 2 <= OptionL.getMaxLevel(skill)) {
				if (i + 2 <= currentLevel) {
					items[track.get(i)] = ClickableItem.empty(getUnlockedLevel(unlocked, i + 2));
				} else if (i + 2 == currentLevel + 1) {
					items[track.get(i)] = ClickableItem.empty(getCurrentLevel(inProgress, i + 2));
				} else {
					items[track.get(i)] = ClickableItem.empty(getLockedLevel(locked, i + 2));
				}
			}
			else {
				if (options.isFillEnabled() && options.getFillItem() != null) {
					items[track.get(i)] = ClickableItem.empty(options.getFillItem());
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
			ItemOption nextPage = options.getItem("next_page");
			contents.set(5, 8, ClickableItem.of(getNextPageItem(nextPage), e -> {
				int page = pagination.next().getPage();
				getInventory(player, skill, page).open(player, page);
			}));
		}

		if (pagination.getPage() - 1 >= 0) {
			ItemOption previousPage = options.getItem("previous_page");
			contents.set(5, 7, ClickableItem.of(getPreviousPageItem(previousPage), e -> {
				int previous = pagination.previous().getPage();
				getInventory(player, skill, previous).open(player, previous);
			}));
		}
		long end = System.nanoTime();
		player.sendMessage("Menu opened in " + ((double) (end - start))/1000000 + " ms");
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
	
	private ItemStack getNextPageItem(ItemOption itemOption) {
		ItemStack item = itemOption.getBaseItem().clone();
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName().replace("{next_page}", Lang.getMessage(MenuMessage.NEXT_PAGE)));
			List<String> lore = new ArrayList<>();
			List<String> baseLore = meta.getLore();
			if (baseLore != null) {
				for (String line : baseLore) {
					lore.add(line.replace("{next_page_click}", Lang.getMessage(MenuMessage.NEXT_PAGE_CLICK)));
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}
	
	private ItemStack getPreviousPageItem(ItemOption itemOption) {
		ItemStack item = itemOption.getBaseItem().clone();
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName().replace("{previous_page}", Lang.getMessage(MenuMessage.PREVIOUS_PAGE)));
			List<String> lore = new ArrayList<>();
			List<String> baseLore = meta.getLore();
			if (baseLore != null) {
				for (String line : baseLore) {
					lore.add(line.replace("{previous_page_click}", Lang.getMessage(MenuMessage.PREVIOUS_PAGE_CLICK)));
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}
	
	private ItemStack getUnlockedLevel(ItemTemplate template, int level) {
		ItemStack item = template.getBaseItem("constant").clone();
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName()
					.replace("{level_unlocked}", Lang.getMessage(MenuMessage.LEVEL_UNLOCKED)
							.replace("{level}", RomanNumber.toRoman(level))));
			List<String> lore = new ArrayList<>();
			List<String> baseLore = meta.getLore();
			if (baseLore != null) {
				for (String line : baseLore) {
					String replacedLore = replaceLore(line, level);
					if (replacedLore != null) {
						lore.add(replacedLore.replace("{unlocked}", Lang.getMessage(MenuMessage.UNLOCKED)));
					}
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}
	
	private ItemStack getCurrentLevel(ItemTemplate template, int level) {
		ItemStack item = template.getBaseItem("constant").clone();
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName()
					.replace("{level_in_progress}", Lang.getMessage(MenuMessage.LEVEL_IN_PROGRESS)
							.replace("{level}", RomanNumber.toRoman(level))));
			List<String> lore = new ArrayList<>();
			NumberFormat nf = new DecimalFormat("##.##");
			double xp = SkillLoader.playerSkills.get(player.getUniqueId()).getXp(skill);
			double xpToNext;
			if (Leveler.levelReqs.size() > level - 2) {
				xpToNext = Leveler.levelReqs.get(level - 2);
			} else {
				xpToNext = 0;
			}
			List<String> baseLore = meta.getLore();
			if (baseLore != null) {
				for (String line : baseLore) {
					String replacedLore = replaceLore(line, level);
					if (replacedLore != null) {
						lore.add(replacedLore.replace("{progress}", Lang.getMessage(MenuMessage.PROGRESS)
								.replace("{percent}", nf.format(xp / xpToNext * 100))
								.replace("{current_xp}", nf.format(xp))
								.replace("{level_xp}", String.valueOf((int) xpToNext)))
								.replace("{in_progress}", Lang.getMessage(MenuMessage.IN_PROGRESS)));
					}
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}
	
	private ItemStack getLockedLevel(ItemTemplate template, int level) {
		ItemStack item = template.getBaseItem("constant").clone();
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName()
					.replace("{level_locked}", Lang.getMessage(MenuMessage.LEVEL_LOCKED)
							.replace("{level}", RomanNumber.toRoman(level))));
			List<String> lore = new ArrayList<>();
			List<String> baseLore = meta.getLore();
			if (baseLore != null) {
				for (String line : baseLore) {
					String replacedLore = replaceLore(line, level);
					if (replacedLore != null) {
						lore.add(replacedLore.replace("{locked}", Lang.getMessage(MenuMessage.LOCKED)));
					}
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}

	private ItemStack getRankItem(ItemOption itemOption) {
		ItemStack item = itemOption.getBaseItem().clone();
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName().replace("{your_ranking}", Lang.getMessage(MenuMessage.YOUR_RANKING)));
			List<String> lore = new ArrayList<>();
			int rank = AureliumSkills.leaderboard.getSkillRank(skill, player.getUniqueId());
			int total = AureliumSkills.leaderboard.getSize();
			NumberFormat nf;
			double percentRank = (double) rank / (double) total * 100;
			if (percentRank < 1) {
				nf = new DecimalFormat("#.###");
			} else {
				nf = new DecimalFormat("#.#");
			}
			List<String> baseLore = meta.getLore();
			if (baseLore != null) {
				for (String line : baseLore) {
					lore.add(line.replace("{rank_out_of}", Lang.getMessage(MenuMessage.RANK_OUT_OF)
							.replace("{rank}", String.valueOf(rank))
							.replace("{total}", String.valueOf(total)))
							.replace("{rank_percent}", Lang.getMessage(MenuMessage.RANK_PERCENT)
									.replace("{percent}", nf.format(percentRank))));
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}

	private String getRewardsOne(int level) {
		if (level%2 != 0) {
			return Lang.getMessage(MenuMessage.REWARDS_ONE)
					.replace("{color}",skill.getPrimaryStat().getColor())
					.replace("{num}", String.valueOf(1))
					.replace("{symbol}", skill.getPrimaryStat().getSymbol())
					.replace("{stat}", skill.getPrimaryStat().getDisplayName());
		}
		return "";
	}

	private String getRewardsTwo(int level) {
		if (level%2 == 0) {
			return Lang.getMessage(MenuMessage.REWARDS_TWO)
					.replace("{color_1}",skill.getPrimaryStat().getColor())
					.replace("{num_1}", String.valueOf(1))
					.replace("{symbol_1}", skill.getPrimaryStat().getSymbol())
					.replace("{stat_1}", skill.getPrimaryStat().getDisplayName())
					.replace("{color_2}",skill.getSecondaryStat().getColor())
					.replace("{num_2}", String.valueOf(1))
					.replace("{symbol_2}", skill.getSecondaryStat().getSymbol())
					.replace("{stat_2}", skill.getSecondaryStat().getDisplayName());
		}
		return "";
	}

	private String getAbilityUnlock(int level) {
		Ability ability = getAbility(level);
		NumberFormat nf = new DecimalFormat("#.#");
		if (ability != null) {
			if (AureliumSkills.abilityOptionManager.isEnabled(ability) && level <= 6) {
				return Lang.getMessage(MenuMessage.ABILITY_UNLOCK)
						.replace("{ability}", ability.getDisplayName())
						.replace("{desc}", ability.getDescription())
						.replace("{value_2}", nf.format(ability.getValue2((level + 3) / 5)))
						.replace("{value}", nf.format(ability.getValue((level + 3) / 5)));
			}
		}
		return "";
	}

	private String getAbilityLevel(int level) {
		Ability ability = getAbility(level);
		NumberFormat nf = new DecimalFormat("#.#");
		if (ability != null) {
			if (AureliumSkills.abilityOptionManager.isEnabled(ability) && level > 6) {
				return Lang.getMessage(MenuMessage.ABILITY_LEVEL)
						.replace("{ability}", ability.getDisplayName())
						.replace("{level}", RomanNumber.toRoman((level + 3) / 5))
						.replace("{desc}", ability.getDescription())
						.replace("{value_2}", nf.format(ability.getValue2((level + 3) / 5)))
						.replace("{value}", nf.format(ability.getValue((level + 3) / 5)));
			}
		}
		return "";
	}

	private String getManaAbilityUnlock(int level) {
		MAbility mAbility = skill.getManaAbility();
		if (level == 7) {
			return Lang.getMessage(MenuMessage.MANA_ABILITY_UNLOCK)
					.replace("{mana_ability}", mAbility.getDisplayName())
					.replace("{desc}", mAbility.getDescription()
							.replace("{value}", String.valueOf(mAbility.getValue(level / 7))));
		}
		return "";
	}

	private String getManaAbilityLevel(int level) {
		MAbility mAbility = skill.getManaAbility();
		if (level != 7) {
			return Lang.getMessage(MenuMessage.MANA_ABILITY_LEVEL)
					.replace("{mana_ability}", mAbility.getDisplayName())
					.replace("{level}", RomanNumber.toRoman(level / 7))
					.replace("{desc}", mAbility.getDescription()
							.replace("{value}", String.valueOf(mAbility.getValue(level / 7))));
		}
		return "";
	}

	private String replaceLore(String line, int level) {
		MAbility mAbility = skill.getManaAbility();
		if (line.startsWith("?hasManaAbility")) {
			if (level % 7 == 0 && AureliumSkills.abilityOptionManager.isEnabled(mAbility)) {
				return line.replace("{level_number}", Lang.getMessage(MenuMessage.LEVEL_NUMBER)
						.replace("{level}", String.valueOf(level)))
						.replace("{rewards_one}", getRewardsOne(level))
						.replace("{rewards_two}", getRewardsTwo(level))
						.replace("{ability_unlock}", getAbilityUnlock(level))
						.replace("{ability_level}", getAbilityLevel(level))
						.replace("{mana_ability_unlock}", getManaAbilityUnlock(level))
						.replace("{mana_ability_level}", getManaAbilityLevel(level))
						.replace("?hasManaAbility", "");
			}
			else {
				return null;
			}
		}
		else if (line.startsWith("?hasAbility")) {
			Ability ability = getAbility(level);
			if (ability != null) {
				if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
					return line.replace("{level_number}", Lang.getMessage(MenuMessage.LEVEL_NUMBER)
							.replace("{level}", String.valueOf(level)))
							.replace("{rewards_one}", getRewardsOne(level))
							.replace("{rewards_two}", getRewardsTwo(level))
							.replace("{ability_unlock}", getAbilityUnlock(level))
							.replace("{ability_level}", getAbilityLevel(level))
							.replace("{mana_ability_unlock}", getManaAbilityUnlock(level))
							.replace("{mana_ability_level}", getManaAbilityLevel(level))
							.replace("?hasAbility", "");
				}
				else {
					return null;
				}
			}
			else {
				return null;
			}
		}
		else {
			return line.replace("{level_number}", Lang.getMessage(MenuMessage.LEVEL_NUMBER)
					.replace("{level}", String.valueOf(level)))
					.replace("{rewards_one}", getRewardsOne(level))
					.replace("{rewards_two}", getRewardsTwo(level))
					.replace("{ability_unlock}", getAbilityUnlock(level))
					.replace("{ability_level}", getAbilityLevel(level))
					.replace("{mana_ability_unlock}", getManaAbilityUnlock(level))
					.replace("{mana_ability_level}", getManaAbilityLevel(level));
		}
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

	private ItemStack getBackItem(ItemOption itemOption) {
		ItemStack item = itemOption.getBaseItem().clone();
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName().replace("{back}", Lang.getMessage(MenuMessage.BACK)));
			List<String> lore = new ArrayList<>();
			List<String> baseLore = meta.getLore();
			if (baseLore != null) {
				for (String line : baseLore) {
					lore.add(line.replace("{back_click}", Lang.getMessage(MenuMessage.BACK_CLICK)));
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}

	private ItemStack getCloseItem(ItemOption itemOption) {
		ItemStack item = itemOption.getBaseItem().clone();
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName().replace("{close}", Lang.getMessage(MenuMessage.CLOSE)));
			item.setItemMeta(meta);
		}
		return item;
	}

	private ItemStack getSkillItem(ItemOption itemOption, Skill skill) {
		ItemStack item = itemOption.getBaseItems().get(skill).clone();
		ItemMeta meta = item.getItemMeta();
		NumberFormat nf = new DecimalFormat("##.##");
		PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
		if (meta != null && playerSkill != null) {
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setDisplayName(meta.getDisplayName()
					.replace("{skill}", skill.getDisplayName())
					.replace("{level}", RomanNumber.toRoman(playerSkill.getSkillLevel(skill))));
			String abilityLevels = Lang.getMessage(MenuMessage.ABILITY_LEVELS);
			int count = 1;
			//Replace message with contexts
			for (Supplier<Ability> supplier : skill.getAbilities()) {
				Ability ability = supplier.get();
				if (ability != null) {
					if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
						if (playerSkill.getAbilityLevel(ability) > 0) {
							abilityLevels = abilityLevels.replace("{ability_" + count + "}", Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY)
									.replace("{ability}", ability.getDisplayName())
									.replace("{level}", RomanNumber.toRoman(playerSkill.getAbilityLevel(ability)))
									.replace("{info}", ability.getMiniDescription()
											.replace("{value}", nf.format(ability.getValue(playerSkill.getAbilityLevel(ability))))
											.replace("{value_2}", nf.format(ability.getValue2(playerSkill.getAbilityLevel(ability))))));
						} else {
							abilityLevels = abilityLevels.replace("{ability_" + count + "}", Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY_LOCKED)
									.replace("{ability}", ability.getDisplayName()));
						}
					} else {
						abilityLevels = abilityLevels.replace("{ability_" + count + "}", "");
					}
					count++;
				}
			}
			for (int i = count; i < 6; i++) {
				abilityLevels = abilityLevels.replace("{ability_" + i + "}", "");
			}
			String manaAbility = "";
			if (playerSkill.getManaAbilityLevel(skill.getManaAbility()) > 0) {
				MAbility mAbility = skill.getManaAbility();
				manaAbility = Lang.getMessage(MenuMessage.MANA_ABILITY)
						.replace("{mana_ability}", mAbility.getDisplayName())
						.replace("{level}", RomanNumber.toRoman(playerSkill.getManaAbilityLevel(mAbility)))
						.replace("{duration}", nf.format(mAbility.getValue(playerSkill.getManaAbilityLevel(mAbility))))
						.replace("{mana_cost}", String.valueOf(mAbility.getManaCost(playerSkill.getManaAbilityLevel(mAbility))))
						.replace("{cooldown}", String.valueOf(mAbility.getCooldown(playerSkill.getManaAbilityLevel(mAbility))));
			}
			String progressToLevel = "";
			String maxLevel = "";
			if (playerSkill.getSkillLevel(skill) < OptionL.getMaxLevel(skill)) {
				int level = playerSkill.getSkillLevel(skill);
				double xp = playerSkill.getXp(skill);
				double xpToNext = Leveler.levelReqs.get(level - 1);
				progressToLevel = Lang.getMessage(MenuMessage.PROGRESS_TO_LEVEL)
						.replace("{level}", RomanNumber.toRoman(level + 1))
						.replace("{percent}", nf.format(xp / xpToNext * 100))
						.replace("{current_xp}", nf.format(xp))
						.replace("{level_xp}", String.valueOf((int) xpToNext));
			}
			else {
				maxLevel = Lang.getMessage(MenuMessage.MAX_LEVEL);
			}
			List<String> lore = new ArrayList<>();
			List<String> baseLore = meta.getLore();
			if (baseLore != null) {
				for (String line : baseLore) {
					lore.add(line.replace("{skill_desc}", skill.getDescription())
							.replace("{primary_stat}", Lang.getMessage(MenuMessage.PRIMARY_STAT)
									.replace("{color}", skill.getPrimaryStat().getColor())
									.replace("{stat}", skill.getPrimaryStat().getDisplayName()))
							.replace("{secondary_stat}", Lang.getMessage(MenuMessage.SECONDARY_STAT)
									.replace("{color}", skill.getSecondaryStat().getColor())
									.replace("{stat}", skill.getSecondaryStat().getDisplayName()))
							.replace("{ability_levels}", abilityLevels)
							.replace("{level}", Lang.getMessage(MenuMessage.LEVEL).replace("{level}", RomanNumber.toRoman(playerSkill.getSkillLevel(skill))))
							.replace("{mana_ability}", manaAbility)
							.replace("{progress_to_level}", progressToLevel)
							.replace("{max_level}", maxLevel));
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}
	
}
