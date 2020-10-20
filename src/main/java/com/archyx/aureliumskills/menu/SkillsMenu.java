package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menu.items.CloseItem;
import com.archyx.aureliumskills.menu.items.ItemType;
import com.archyx.aureliumskills.menu.items.YourSkillsItem;
import com.archyx.aureliumskills.menu.templates.SkillTemplate;
import com.archyx.aureliumskills.menu.templates.TemplateType;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

public class SkillsMenu implements InventoryProvider{

	private final Locale locale;
	private final MenuOption options;

	public SkillsMenu(Locale locale) {
		this.locale = locale;
		options = AureliumSkills.getMenuLoader().getMenu(MenuType.SKILLS);
	}

	public void init(Player player, InventoryContents contents) {
		// Fill item
		if (options.isFillEnabled()) {
			contents.fill(ClickableItem.empty(options.getFillItem()));
		}
		// Close item
		CloseItem closeItem = (CloseItem) options.getItem(ItemType.CLOSE);
		contents.set(closeItem.getPos(), ClickableItem.of(closeItem.getItem(locale), e -> player.closeInventory()));
		// Your skills item
		YourSkillsItem yourSkillsItem = (YourSkillsItem) options.getItem(ItemType.YOUR_SKILLS);
		contents.set(yourSkillsItem.getPos(), ClickableItem.empty(yourSkillsItem.getItem(player, locale)));
		// Skill items
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
			SkillTemplate skillTemplate = (SkillTemplate) options.getTemplate(TemplateType.SKILL);
			if (OptionL.isEnabled(Skill.FARMING)) {
				contents.set(skillTemplate.getPosition(Skill.FARMING), ClickableItem.of(skillTemplate.getItem(Skill.FARMING, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.farming")) {
						int page = getPage(Skill.FARMING, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.FARMING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.FORAGING)) {
				contents.set(skillTemplate.getPosition(Skill.FORAGING), ClickableItem.of(skillTemplate.getItem(Skill.FORAGING, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.foraging")) {
						int page = getPage(Skill.FORAGING, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.FORAGING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.MINING)) {
				contents.set(skillTemplate.getPosition(Skill.MINING), ClickableItem.of(skillTemplate.getItem(Skill.MINING, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.mining")) {
						int page = getPage(Skill.MINING, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.MINING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.FISHING)) {
				contents.set(skillTemplate.getPosition(Skill.FISHING), ClickableItem.of(skillTemplate.getItem(Skill.FISHING, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.fishing")) {
						int page = getPage(Skill.FISHING, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.FISHING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.EXCAVATION)) {
				contents.set(skillTemplate.getPosition(Skill.EXCAVATION), ClickableItem.of(skillTemplate.getItem(Skill.EXCAVATION, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.excavation")) {
						int page = getPage(Skill.EXCAVATION, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.EXCAVATION, page).open(player, page);
					}
				}));
			}
			//Combat Skills
			if (OptionL.isEnabled(Skill.ARCHERY)) {
				contents.set(skillTemplate.getPosition(Skill.ARCHERY), ClickableItem.of(skillTemplate.getItem(Skill.ARCHERY, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.archery")) {
						int page = getPage(Skill.ARCHERY, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.ARCHERY, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.DEFENSE)) {
				contents.set(skillTemplate.getPosition(Skill.DEFENSE), ClickableItem.of(skillTemplate.getItem(Skill.DEFENSE, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.defense")) {
						int page = getPage(Skill.DEFENSE, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.DEFENSE, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.FIGHTING)) {
				contents.set(skillTemplate.getPosition(Skill.FIGHTING), ClickableItem.of(skillTemplate.getItem(Skill.FIGHTING, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.fighting")) {
						int page = getPage(Skill.FIGHTING, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.FIGHTING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.ENDURANCE)) {
				contents.set(skillTemplate.getPosition(Skill.ENDURANCE), ClickableItem.of(skillTemplate.getItem(Skill.ENDURANCE, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.endurance")) {
						int page = getPage(Skill.ENDURANCE, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.ENDURANCE, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.AGILITY)) {
				contents.set(skillTemplate.getPosition(Skill.AGILITY), ClickableItem.of(skillTemplate.getItem(Skill.AGILITY, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.agility")) {
						int page = getPage(Skill.AGILITY, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.AGILITY, page).open(player, page);
					}
				}));
			}
			//Magic Skills
			if (OptionL.isEnabled(Skill.ALCHEMY)) {
				contents.set(skillTemplate.getPosition(Skill.ALCHEMY), ClickableItem.of(skillTemplate.getItem(Skill.ALCHEMY, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.alchemy")) {
						int page = getPage(Skill.ALCHEMY, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.ALCHEMY, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.ENCHANTING)) {
				contents.set(skillTemplate.getPosition(Skill.ENCHANTING), ClickableItem.of(skillTemplate.getItem(Skill.ENCHANTING, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.enchanting")) {
						int page = getPage(Skill.ENCHANTING, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.ENCHANTING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.SORCERY)) {
				contents.set(skillTemplate.getPosition(Skill.SORCERY), ClickableItem.of(skillTemplate.getItem(Skill.SORCERY, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.sorcery")) {
						int page = getPage(Skill.SORCERY, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.SORCERY, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.HEALING)) {
				contents.set(skillTemplate.getPosition(Skill.HEALING), ClickableItem.of(skillTemplate.getItem(Skill.HEALING, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.healing")) {
						int page = getPage(Skill.HEALING, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.HEALING, page).open(player, page);
					}
				}));
			}
			if (OptionL.isEnabled(Skill.FORGING)) {
				contents.set(skillTemplate.getPosition(Skill.FORGING), ClickableItem.of(skillTemplate.getItem(Skill.FORGING, playerSkill, locale), e -> {
					if (player.hasPermission("aureliumskills.forging")) {
						int page = getPage(Skill.FORGING, playerSkill);
						LevelProgressionMenu.getInventory(player, Skill.FORGING, page).open(player, page);
					}
				}));
			}
		}
		else {
			player.closeInventory();
			player.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.RED + Lang.getMessage(CommandMessage.NO_PROFILE, locale));
		}
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

	public static SmartInventory getInventory(Player player) {
		Locale locale = Lang.getLanguage(player);
		return SmartInventory.builder()
				.provider(new SkillsMenu(locale))
				.size(5, 9)
				.title(Lang.getMessage(MenuMessage.SKILLS_MENU_TITLE, locale))
				.manager(AureliumSkills.invManager)
				.build();
	}

}
