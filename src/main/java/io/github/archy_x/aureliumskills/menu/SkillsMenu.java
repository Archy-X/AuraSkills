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
		//Gathering Skills
		contents.set(SlotPos.of(1, 2), ClickableItem.of(Skill.FARMING.getMenuItem(player, true), e -> {
			if (e.isLeftClick()) {
				LevelProgressionMenu.getInventory(player, Skill.FARMING, 0).open(player);
			}
			else if (e.isRightClick() ){
				SkillTreeMenu.getInventory(SkillTree.FARMING, false).open(player);
			}
		}));
		contents.set(SlotPos.of(1, 3), ClickableItem.of(Skill.FORAGING.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.FORAGING, 0).open(player);
		}));
		contents.set(SlotPos.of(1, 4), ClickableItem.of(Skill.MINING.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.MINING, 0).open(player);		
		}));
		contents.set(SlotPos.of(1, 5), ClickableItem.of(Skill.FISHING.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.FISHING, 0).open(player);				
		}));
		contents.set(SlotPos.of(1, 6), ClickableItem.of(Skill.EXCAVATION.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.EXCAVATION, 0).open(player);						
		}));
		//Combat Skills
		contents.set(SlotPos.of(2, 2), ClickableItem.of(Skill.ARCHERY.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.ARCHERY, 0).open(player);								
		}));
		contents.set(SlotPos.of(2, 3), ClickableItem.of(Skill.DEFENSE.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.DEFENSE, 0).open(player);										
		}));
		contents.set(SlotPos.of(2, 4), ClickableItem.of(Skill.FIGHTING.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.FIGHTING, 0).open(player);										
		}));
		contents.set(SlotPos.of(2, 5), ClickableItem.of(Skill.ENDURANCE.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.ENDURANCE, 0).open(player);										
		}));
		contents.set(SlotPos.of(2, 6), ClickableItem.of(Skill.AGILITY.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.AGILITY, 0).open(player);										
		}));
		//Magic Skills
		contents.set(SlotPos.of(3, 2), ClickableItem.of(Skill.ALCHEMY.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.ALCHEMY, 0).open(player);								
		}));
		contents.set(SlotPos.of(3, 3), ClickableItem.of(Skill.ENCHANTING.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.ENCHANTING, 0).open(player);										
		}));
		contents.set(SlotPos.of(3, 4), ClickableItem.of(Skill.SORCERY.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.SORCERY, 0).open(player);										
		}));
		contents.set(SlotPos.of(3, 5), ClickableItem.of(Skill.HEALING.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.HEALING, 0).open(player);										
		}));
		contents.set(SlotPos.of(3, 6), ClickableItem.of(Skill.FORGING.getMenuItem(player, true), e -> {
			LevelProgressionMenu.getInventory(player, Skill.FORGING, 0).open(player);										
		}));
	}

	public void update(Player player, InventoryContents contents) {
		// TODO Auto-generated method stub
		
	}
	
	private ItemStack getSkillsItem() {
		ItemStack item = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Your Skills - " + ChatColor.GOLD + player.getName());
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> lore = new LinkedList<String>();
		lore.add(ChatColor.GRAY + "Upgrade Skills by doing various tasks to unlock");
		lore.add(ChatColor.GRAY + "valuable stat boosts, abilities, and more!");
		lore.add(" ");
		lore.add(ChatColor.YELLOW + "Hover over a Skill for more information!");
		lore.add(ChatColor.YELLOW + "Click on a Skill to view level progression!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static SmartInventory getInventory(Player player) {
		return SmartInventory.builder()
				.provider(new SkillsMenu(player))
				.size(5, 9)
				.title("Your Skills")
				.manager(AureliumSkills.invManager)
				.build();
	}

}
