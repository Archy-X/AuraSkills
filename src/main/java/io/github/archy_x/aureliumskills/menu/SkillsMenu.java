package io.github.archy_x.aureliumskills.menu;

import java.util.LinkedList;
import java.util.List;

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
import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Lang;
import io.github.archy_x.aureliumskills.Message;
import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.skilltree.SkillTree;

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
		//Gathering Skills
		if (Options.isEnabled(Skill.FARMING)) {
			contents.set(SlotPos.of(1, pos), ClickableItem.of(Skill.FARMING.getMenuItem(player, true), e -> {
				if (e.isLeftClick()) {
					LevelProgressionMenu.getInventory(player, Skill.FARMING, 0).open(player);
				}
				else if (e.isRightClick() ){
					if (Options.getBooleanOption(Setting.ENABLE_SKILL_POINTS)) {
						SkillTreeMenu.getInventory(SkillTree.FARMING, false).open(player);
					}
				}
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.FORAGING)) {
			contents.set(SlotPos.of(1, pos), ClickableItem.of(Skill.FORAGING.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.FORAGING, 0).open(player);
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.MINING)) {
			contents.set(SlotPos.of(1, pos), ClickableItem.of(Skill.MINING.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.MINING, 0).open(player);		
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.FISHING)) {
			contents.set(SlotPos.of(1, pos), ClickableItem.of(Skill.FISHING.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.FISHING, 0).open(player);				
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.EXCAVATION)) {
			contents.set(SlotPos.of(1, pos), ClickableItem.of(Skill.EXCAVATION.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.EXCAVATION, 0).open(player);						
			}));
		}
		pos = 2;
		//Combat Skills
		if (Options.isEnabled(Skill.ARCHERY)) {
			contents.set(SlotPos.of(2, pos), ClickableItem.of(Skill.ARCHERY.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.ARCHERY, 0).open(player);								
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.DEFENSE)) {
			contents.set(SlotPos.of(2, pos), ClickableItem.of(Skill.DEFENSE.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.DEFENSE, 0).open(player);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.FIGHTING)) {
			contents.set(SlotPos.of(2, pos), ClickableItem.of(Skill.FIGHTING.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.FIGHTING, 0).open(player);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.ENDURANCE)) {
			contents.set(SlotPos.of(2, pos), ClickableItem.of(Skill.ENDURANCE.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.ENDURANCE, 0).open(player);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.AGILITY)) {
			contents.set(SlotPos.of(2, pos), ClickableItem.of(Skill.AGILITY.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.AGILITY, 0).open(player);										
			}));
		}
		pos = 2;
		//Magic Skills
		if (Options.isEnabled(Skill.ALCHEMY)) {
			contents.set(SlotPos.of(3, pos), ClickableItem.of(Skill.ALCHEMY.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.ALCHEMY, 0).open(player);								
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.ENCHANTING)) {
			contents.set(SlotPos.of(3, pos), ClickableItem.of(Skill.ENCHANTING.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.ENCHANTING, 0).open(player);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.SORCERY)) {
			contents.set(SlotPos.of(3, pos), ClickableItem.of(Skill.SORCERY.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.SORCERY, 0).open(player);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.HEALING)) {
			contents.set(SlotPos.of(3, pos), ClickableItem.of(Skill.HEALING.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.HEALING, 0).open(player);										
			}));
			pos++;
		}
		if (Options.isEnabled(Skill.FORGING)) {
			contents.set(SlotPos.of(3, pos), ClickableItem.of(Skill.FORGING.getMenuItem(player, true), e -> {
				LevelProgressionMenu.getInventory(player, Skill.FORGING, 0).open(player);										
			}));
		}
	}

	public void update(Player player, InventoryContents contents) {
		// TODO Auto-generated method stub
		
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
