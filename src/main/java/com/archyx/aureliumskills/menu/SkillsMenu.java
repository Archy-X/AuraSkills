package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.CommandMessage;
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
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SkillsMenu implements InventoryProvider{

	private final Player player;
	private final MenuOption options;

	public SkillsMenu(Player player) {
		this.player = player;
		options = AureliumSkills.getMenuLoader().getMenu("skills_menu");
	}

	public void init(Player player, InventoryContents contents) {
		long start = System.nanoTime();
		// Fill item
		if (options.isFillEnabled()) {
			contents.fill(ClickableItem.empty(options.getFillItem()));
		}
		// Close item
		ItemOption close = options.getItem("close");
		contents.set(SlotPos.of(close.getRow(), close.getColumn()), ClickableItem.of(getCloseItem(), e -> player.closeInventory()));
		// Your skills item
		ItemOption skills = options.getItem("your_skills");
		contents.set(SlotPos.of(skills.getRow(), skills.getColumn()), ClickableItem.empty(getYourSkillsItem()));
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
			ItemTemplate template = options.getTemplate("skill");
			if (OptionL.isEnabled(Skill.FARMING)) {
				contents.set(template.getPosition(Skill.FARMING), ClickableItem.of(getSkillItem(template, Skill.FARMING), e -> {
					if (player.hasPermission("aureliumskills.farming")) {
						int page = getPage(Skill.FARMING, skill);
						LevelProgressionMenu.getInventory(player, Skill.FARMING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.FORAGING)) {
				contents.set(template.getPosition(Skill.FORAGING), ClickableItem.of(getSkillItem(template, Skill.FORAGING), e -> {
					if (player.hasPermission("aureliumskills.foraging")) {
						int page = getPage(Skill.FORAGING, skill);
						LevelProgressionMenu.getInventory(player, Skill.FORAGING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.MINING)) {
				contents.set(template.getPosition(Skill.MINING), ClickableItem.of(getSkillItem(template, Skill.MINING), e -> {
					if (player.hasPermission("aureliumskills.mining")) {
						int page = getPage(Skill.MINING, skill);
						LevelProgressionMenu.getInventory(player, Skill.MINING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.FISHING)) {
				contents.set(template.getPosition(Skill.FISHING), ClickableItem.of(getSkillItem(template, Skill.FISHING), e -> {
					if (player.hasPermission("aureliumskills.fishing")) {
						int page = getPage(Skill.FISHING, skill);
						LevelProgressionMenu.getInventory(player, Skill.FISHING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.EXCAVATION)) {
				contents.set(template.getPosition(Skill.EXCAVATION), ClickableItem.of(getSkillItem(template, Skill.EXCAVATION), e -> {
					if (player.hasPermission("aureliumskills.excavation")) {
						int page = getPage(Skill.EXCAVATION, skill);
						LevelProgressionMenu.getInventory(player, Skill.EXCAVATION, page).open(player, page);
					}
				}));
			}
			//Combat Skills
			if (OptionL.isEnabled(Skill.ARCHERY)) {
				contents.set(template.getPosition(Skill.ARCHERY), ClickableItem.of(getSkillItem(template, Skill.ARCHERY), e -> {
					if (player.hasPermission("aureliumskills.archery")) {
						int page = getPage(Skill.ARCHERY, skill);
						LevelProgressionMenu.getInventory(player, Skill.ARCHERY, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.DEFENSE)) {
				contents.set(template.getPosition(Skill.DEFENSE), ClickableItem.of(getSkillItem(template, Skill.DEFENSE), e -> {
					if (player.hasPermission("aureliumskills.defense")) {
						int page = getPage(Skill.DEFENSE, skill);
						LevelProgressionMenu.getInventory(player, Skill.DEFENSE, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.FIGHTING)) {
				contents.set(template.getPosition(Skill.FIGHTING), ClickableItem.of(getSkillItem(template, Skill.FIGHTING), e -> {
					if (player.hasPermission("aureliumskills.fighting")) {
						int page = getPage(Skill.FIGHTING, skill);
						LevelProgressionMenu.getInventory(player, Skill.FIGHTING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.ENDURANCE)) {
				contents.set(template.getPosition(Skill.ENDURANCE), ClickableItem.of(getSkillItem(template, Skill.ENDURANCE), e -> {
					if (player.hasPermission("aureliumskills.endurance")) {
						int page = getPage(Skill.ENDURANCE, skill);
						LevelProgressionMenu.getInventory(player, Skill.ENDURANCE, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.AGILITY)) {
				contents.set(template.getPosition(Skill.AGILITY), ClickableItem.of(getSkillItem(template, Skill.AGILITY), e -> {
					if (player.hasPermission("aureliumskills.agility")) {
						int page = getPage(Skill.AGILITY, skill);
						LevelProgressionMenu.getInventory(player, Skill.AGILITY, page).open(player, page);
					}
				}));
			}
			//Magic Skills
			if (OptionL.isEnabled(Skill.ALCHEMY)) {
				contents.set(template.getPosition(Skill.ALCHEMY), ClickableItem.of(getSkillItem(template, Skill.ALCHEMY), e -> {
					if (player.hasPermission("aureliumskills.alchemy")) {
						int page = getPage(Skill.ALCHEMY, skill);
						LevelProgressionMenu.getInventory(player, Skill.ALCHEMY, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.ENCHANTING)) {
				contents.set(template.getPosition(Skill.ENCHANTING), ClickableItem.of(getSkillItem(template, Skill.ENCHANTING), e -> {
					if (player.hasPermission("aureliumskills.enchanting")) {
						int page = getPage(Skill.ENCHANTING, skill);
						LevelProgressionMenu.getInventory(player, Skill.ENCHANTING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.SORCERY)) {
				contents.set(template.getPosition(Skill.SORCERY), ClickableItem.of(getSkillItem(template, Skill.SORCERY), e -> {
					if (player.hasPermission("aureliumskills.sorcery")) {
						int page = getPage(Skill.SORCERY, skill);
						LevelProgressionMenu.getInventory(player, Skill.SORCERY, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.HEALING)) {
				contents.set(template.getPosition(Skill.HEALING), ClickableItem.of(getSkillItem(template, Skill.HEALING), e -> {
					if (player.hasPermission("aureliumskills.healing")) {
						int page = getPage(Skill.HEALING, skill);
						LevelProgressionMenu.getInventory(player, Skill.HEALING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.FORGING)) {
				contents.set(template.getPosition(Skill.FORGING), ClickableItem.of(getSkillItem(template, Skill.FORGING), e -> {
					if (player.hasPermission("aureliumskills.forging")) {
						int page = getPage(Skill.FORGING, skill);
						LevelProgressionMenu.getInventory(player, Skill.FORGING, page).open(player, page);
					}
				}));
			}
		}
		else {
			player.closeInventory();
			player.sendMessage(AureliumSkills.tag + ChatColor.RED + Lang.getMessage(CommandMessage.NO_PROFILE));
		}
		long end = System.nanoTime();
		player.sendMessage("Menu opened in " + ((double) (end - start))/1000000 + " ms");
	}

	public void update(Player player, InventoryContents contents) {

	}

	public static int getPage(Skill skill, PlayerSkill playerSkill) {
		int page = (playerSkill.getSkillLevel(skill) - 2) / 24;
		int maxLevelPage = (OptionL.getMaxLevel(skill) - 2) / 24;
		if (page >= 4) {
			page = 3;
		}
		if (page > maxLevelPage) {
			page = maxLevelPage;
		}
		return page;
	}

	private ItemStack getSkillItem(ItemTemplate template, Skill skill) {
		ItemStack item = template.getBaseItem(skill).clone();
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
							.replace("{max_level}", maxLevel)
							.replace("{skill_click}", Lang.getMessage(MenuMessage.SKILL_CLICK)));
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}

	private ItemStack getCloseItem() {
		ItemOption itemOption = options.getItem("close");
		ItemStack item = itemOption.getBaseItem().clone();
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName().replace("{close}", Lang.getMessage(MenuMessage.CLOSE)));
			item.setItemMeta(meta);
		}
		return item;
	}

	private ItemStack getYourSkillsItem() {
		ItemOption itemOption = options.getItem("your_skills");
		ItemStack item = itemOption.getBaseItem().clone();
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(meta.getDisplayName()
					.replace("{your_skills}", Lang.getMessage(MenuMessage.YOUR_SKILLS)
							.replace("{player}", player.getName())));
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			List<String> lore = new ArrayList<>();
			List<String> baseLore = meta.getLore();
			if (baseLore != null) {
				for (String line : baseLore) {
					lore.add(line.replace("{your_skills_desc}", Lang.getMessage(MenuMessage.YOUR_SKILLS_DESC))
							.replace("{your_skills_hover}", Lang.getMessage(MenuMessage.YOUR_SKILLS_HOVER))
							.replace("{your_skills_click}", Lang.getMessage(MenuMessage.YOUR_SKILLS_CLICK)));
				}
			}
			meta.setLore(ItemUtils.formatLore(lore));
			item.setItemMeta(meta);
		}
		return item;
	}
	
	public static SmartInventory getInventory(Player player) {
		return SmartInventory.builder()
				.provider(new SkillsMenu(player))
				.size(5, 9)
				.title(Lang.getMessage(MenuMessage.SKILLS_MENU_TITLE))
				.manager(AureliumSkills.invManager)
				.build();
	}

}
