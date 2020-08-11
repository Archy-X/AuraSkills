package com.archyx.aureliumskills.menu;

import java.util.LinkedList;
import java.util.List;

import com.archyx.aureliumskills.lang.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;

public class SkillsMenu implements InventoryProvider{

	private Player player;
	
	public SkillsMenu(Player player) {
		this.player = player;
	}

	public void init(Player player, InventoryContents contents) {
		contents.fill(ClickableItem.empty(MenuItems.getEmptyPane()));
		contents.set(SlotPos.of(4, 8), ClickableItem.of(MenuItems.getCloseButton(), e -> {
			player.closeInventory();
		}));
		contents.set(SlotPos.of(0, 0), ClickableItem.empty(getSkillsItem()));
		int pos = 2;
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		//Gathering Skills
		if (Options.isEnabled(Skill.FARMING)) {
			contents.set(SlotPos.of(1, pos), ClickableItem.of(Skill.FARMING.getMenuItem(player, true), e -> {
				int page = getPage(Skill.FARMING, skill);
				LevelProgressionMenu.getInventory(player, Skill.FARMING, page).open(player, page);
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.FORAGING)) {
			contents.set(SlotPos.of(1, pos), ClickableItem.of(Skill.FORAGING.getMenuItem(player, true), e -> {
				int page = getPage(Skill.FORAGING, skill);
				LevelProgressionMenu.getInventory(player, Skill.FORAGING, page).open(player, page);
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.MINING)) {
			contents.set(SlotPos.of(1, pos), ClickableItem.of(Skill.MINING.getMenuItem(player, true), e -> {
				int page = getPage(Skill.MINING, skill);
				LevelProgressionMenu.getInventory(player, Skill.MINING, page).open(player, page);		
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.FISHING)) {
			contents.set(SlotPos.of(1, pos), ClickableItem.of(Skill.FISHING.getMenuItem(player, true), e -> {
				int page = getPage(Skill.FISHING, skill);
				LevelProgressionMenu.getInventory(player, Skill.FISHING, page).open(player, page);				
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.EXCAVATION)) {
			contents.set(SlotPos.of(1, pos), ClickableItem.of(Skill.EXCAVATION.getMenuItem(player, true), e -> {
				int page = getPage(Skill.EXCAVATION, skill);
				LevelProgressionMenu.getInventory(player, Skill.EXCAVATION, page).open(player, page);						
			}));
		}
		pos = 2;
		//Combat Skills
		if (Options.isEnabled(Skill.ARCHERY)) {
			contents.set(SlotPos.of(2, pos), ClickableItem.of(Skill.ARCHERY.getMenuItem(player, true), e -> {
				int page = getPage(Skill.ARCHERY, skill);
				LevelProgressionMenu.getInventory(player, Skill.ARCHERY, page).open(player, page);								
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.DEFENSE)) {
			contents.set(SlotPos.of(2, pos), ClickableItem.of(Skill.DEFENSE.getMenuItem(player, true), e -> {
				int page = getPage(Skill.DEFENSE, skill);
				LevelProgressionMenu.getInventory(player, Skill.DEFENSE, page).open(player, page);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.FIGHTING)) {
			contents.set(SlotPos.of(2, pos), ClickableItem.of(Skill.FIGHTING.getMenuItem(player, true), e -> {
				int page = getPage(Skill.FIGHTING, skill);
				LevelProgressionMenu.getInventory(player, Skill.FIGHTING, page).open(player, page);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.ENDURANCE)) {
			contents.set(SlotPos.of(2, pos), ClickableItem.of(Skill.ENDURANCE.getMenuItem(player, true), e -> {
				int page = getPage(Skill.ENDURANCE, skill);
				LevelProgressionMenu.getInventory(player, Skill.ENDURANCE, page).open(player, page);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.AGILITY)) {
			contents.set(SlotPos.of(2, pos), ClickableItem.of(Skill.AGILITY.getMenuItem(player, true), e -> {
				int page = getPage(Skill.AGILITY, skill);
				LevelProgressionMenu.getInventory(player, Skill.AGILITY, page).open(player, page);										
			}));
		}
		pos = 2;
		//Magic Skills
		if (Options.isEnabled(Skill.ALCHEMY)) {
			contents.set(SlotPos.of(3, pos), ClickableItem.of(Skill.ALCHEMY.getMenuItem(player, true), e -> {
				int page = getPage(Skill.ALCHEMY, skill);
				LevelProgressionMenu.getInventory(player, Skill.ALCHEMY, page).open(player, page);								
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.ENCHANTING)) {
			contents.set(SlotPos.of(3, pos), ClickableItem.of(Skill.ENCHANTING.getMenuItem(player, true), e -> {
				int page = getPage(Skill.ENCHANTING, skill);
				LevelProgressionMenu.getInventory(player, Skill.ENCHANTING, page).open(player, page);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.SORCERY)) {
			contents.set(SlotPos.of(3, pos), ClickableItem.of(Skill.SORCERY.getMenuItem(player, true), e -> {
				int page = getPage(Skill.SORCERY, skill);
				LevelProgressionMenu.getInventory(player, Skill.SORCERY, page).open(player, page);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.HEALING)) {
			contents.set(SlotPos.of(3, pos), ClickableItem.of(Skill.HEALING.getMenuItem(player, true), e -> {
				int page = getPage(Skill.HEALING, skill);
				LevelProgressionMenu.getInventory(player, Skill.HEALING, page).open(player, page);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.FORGING)) {
			contents.set(SlotPos.of(3, pos), ClickableItem.of(Skill.FORGING.getMenuItem(player, true), e -> {
				int page = getPage(Skill.FORGING, skill);
				LevelProgressionMenu.getInventory(player, Skill.FORGING, page).open(player, page);										
			}));
		}
	}

	public void update(Player player, InventoryContents contents) {
		// TODO Auto-generated method stub
		
	}
	
	public static int getPage(Skill skill, PlayerSkill playerSkill) {
		int page = (playerSkill.getSkillLevel(skill) - 2) / 24;
		if (page >= LevelProgressionMenu.pages) {
			page = LevelProgressionMenu.pages - 1;
		}
		return page;
	}
	
	private ItemStack getSkillsItem() {
		ItemStack item = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + Lang.getMessage(Message.YOUR_SKILLS) + " - " + ChatColor.GOLD + player.getName());
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> lore = new LinkedList<String>();
		String fullDesc = Lang.getMessage(Message.YOUR_SKILLS_DESCRIPTION);
		String[] splitDesc = fullDesc.replaceAll("(?:\\s*)(.{1,"+ 38 +"})(?:\\s+|\\s*$)", "$1\n").split("\n");
		for (String s : splitDesc) {
			lore.add(ChatColor.GRAY + s);
		}
		lore.add(" ");
		lore.add(ChatColor.YELLOW + Lang.getMessage(Message.SKILL_HOVER));
		lore.add(ChatColor.YELLOW + Lang.getMessage(Message.SKILL_CLICK));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static SmartInventory getInventory(Player player) {
		return SmartInventory.builder()
				.provider(new SkillsMenu(player))
				.size(5, 9)
				.title(Lang.getMessage(Message.YOUR_SKILLS))
				.manager(AureliumSkills.invManager)
				.build();
	}

}
