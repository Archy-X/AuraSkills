package io.github.archy_x.aureliumskills.menu;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.skills.PlayerSkill;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.skills.abilities.Ability;
import io.github.archy_x.aureliumskills.skills.skilltree.SkillTree;
import io.github.archy_x.aureliumskills.util.RomanNumber;
import io.github.archy_x.aureliumskills.util.XMaterial;

public class SkillTreeMenu implements InventoryProvider {

	private SkillTree skillTree;
	private List<Integer> track = new LinkedList<Integer>();
	private boolean backToLevelMenu;
	
	public SkillTreeMenu(SkillTree skillTree, boolean backToLevelMenu) {
		this.skillTree = skillTree;
		this.backToLevelMenu = backToLevelMenu;
		track.add(49); 
		track.add(40); 
		track.add(30); track.add(32); 
		track.add(20); track.add(22); track.add(24); 
		track.add(10); track.add(13); track.add(16);
		track.add(0); track.add(2); track.add(4); track.add(6); track.add(8);
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		contents.fill(ClickableItem.empty(MenuItems.getEmptyPane()));
		if (backToLevelMenu == false) {
			contents.set(SlotPos.of(5, 7), ClickableItem.of(MenuItems.getBackButton("Back to Skills Menu"), e -> {
				SkillsMenu.getInventory(player).open(player);
			}));
		}
		else {
			contents.set(SlotPos.of(5, 7), ClickableItem.of(MenuItems.getBackButton("Back to Level Progression Menu"), e -> {
				LevelProgressionMenu.getInventory(player, Skill.valueOf(skillTree.toString()), 0).open(player);
			}));
		}
		contents.set(SlotPos.of(5, 8), ClickableItem.of(MenuItems.getCloseButton(), e -> {
			player.closeInventory();
		}));
		//Set all nodes to locked pane
		for (int i = 0; i < skillTree.getAbilities().length; i++) {
			int row = track.get(i)/9;
			int column = track.get(i)%9;
			if (skillTree.getAbility(i) != Ability.NULL) {
				contents.set(row, column, ClickableItem.empty(getLockedAbility()));
			}
			else {
				contents.set(row, column, ClickableItem.empty(getNullPane()));
			}
		}
		//Sets qualified nodes to click to unlock pane
		setQualifiedNodes(contents, skill, player);
		//Sets unlocked abilities to ability item
		setUpgradableNodes(contents, skill, player);
	}
	
	@Override
	public void update(Player player, InventoryContents contents) {
		
		
	}
	
	public static SmartInventory getInventory(SkillTree skillTree, boolean backToLevelMenu) {
		return SmartInventory.builder()
				.provider(new SkillTreeMenu(skillTree, backToLevelMenu))
				.size(6, 9)
				.title("Skill Tree - " + StringUtils.capitalize(skillTree.toString().toLowerCase()))
				.manager(AureliumSkills.invManager)
				.build();
	}
	
	public void upgradeAbility(Player player, int index) {
		if (skillTree.getAbility(index) != Ability.NULL) {
			PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
			if (skill.getAbilityLevel(skillTree.getAbility(index)) < skillTree.getAbility(index).getMaxLevel()) {
				if (skill.getSkillPoints(skillTree.getSkill()) >= skillTree.getAbility(index).getCostPerLevel()) {
					skill.levelUpAbility(skillTree.getAbility(index));
					skill.setSkillPoints(skillTree.getSkill(), skill.getSkillPoints(skillTree.getSkill()) - skillTree.getAbility(index).getCostPerLevel());
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
					getInventory(skillTree, backToLevelMenu).open(player);
				}
			}
		}
	}
	
	public void unlockAbility(Player player, int index) {
		if (skillTree.getAbility(index) != Ability.NULL) {
			PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
			if (skill.getSkillPoints(skillTree.getSkill()) >= skillTree.getAbility(index).getUnlockCost()) {
				skill.levelUpAbility(skillTree.getAbility(index));
				skill.setSkillPoints(skillTree.getSkill(), skill.getSkillPoints(skillTree.getSkill()) - skillTree.getAbility(index).getUnlockCost());
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
				getInventory(skillTree, backToLevelMenu).open(player);
			}
		}
	}
	
	private void setUpgradableNodes(InventoryContents contents, PlayerSkill skill, Player player) {
		if (skill.getAbilityLevel(skillTree.getAbility(0)) > 0) {
			contents.set(track.get(0)/9, track.get(0)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(0), player), e -> upgradeAbility(player, 0)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(1)) > 0) {
			contents.set(track.get(1)/9, track.get(1)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(1), player), e -> upgradeAbility(player, 1)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(2)) > 0) {
			contents.set(track.get(2)/9, track.get(2)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(2), player), e -> upgradeAbility(player, 2)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(3)) > 0) {
			contents.set(track.get(3)/9, track.get(3)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(3), player), e -> upgradeAbility(player, 3)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(4)) > 0) {
			contents.set(track.get(4)/9, track.get(4)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(4), player), e -> upgradeAbility(player, 4)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(5)) > 0) {
			contents.set(track.get(5)/9, track.get(5)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(5), player), e -> upgradeAbility(player, 5)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(6)) > 0) {
			contents.set(track.get(6)/9, track.get(6)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(6), player), e -> upgradeAbility(player, 6)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(7)) > 0) {
			contents.set(track.get(7)/9, track.get(7)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(7), player), e -> upgradeAbility(player, 7)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(8)) > 0) {
			contents.set(track.get(8)/9, track.get(8)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(8), player), e -> upgradeAbility(player, 8)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(9)) > 0) {
			contents.set(track.get(9)/9, track.get(9)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(9), player), e -> upgradeAbility(player, 9)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(10)) > 0) {
			contents.set(track.get(10)/9, track.get(10)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(10), player), e -> upgradeAbility(player, 10)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(11)) > 0) {
			contents.set(track.get(11)/9, track.get(11)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(11), player), e -> upgradeAbility(player, 11)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(12)) > 0) {
			contents.set(track.get(12)/9, track.get(12)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(12), player), e -> upgradeAbility(player, 12)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(13)) > 0) {
			contents.set(track.get(13)/9, track.get(13)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(13), player), e -> upgradeAbility(player, 13)));
		}
		if (skill.getAbilityLevel(skillTree.getAbility(14)) > 0) {
			contents.set(track.get(14)/9, track.get(14)%9, ClickableItem.of(getAbilityItem(skillTree.getAbility(14), player), e -> upgradeAbility(player, 14)));
		}
	}
	
	
	private void setQualifiedNodes(InventoryContents contents, PlayerSkill skill, Player player) {
		contents.set(track.get(0)/9, track.get(0)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(0), player), e -> unlockAbility(player, 0)));
		if (skill.getAbilityLevel(skillTree.getAbility(0)) > 0) {
			contents.set(track.get(1)/9, track.get(1)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(1), player), e -> unlockAbility(player, 1)));
			if (skill.getAbilityLevel(skillTree.getAbility(1)) > 0) {
				contents.set(track.get(2)/9, track.get(2)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(2), player), e -> unlockAbility(player, 2)));
				contents.set(track.get(3)/9, track.get(3)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(3), player), e -> unlockAbility(player, 3)));
				if (skill.getAbilityLevel(skillTree.getAbility(2)) > 0) {
					contents.set(track.get(4)/9, track.get(4)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(4), player), e -> unlockAbility(player, 4)));
					if (skill.getAbilityLevel(skillTree.getAbility(4)) > 0) {
						contents.set(track.get(7)/9, track.get(7)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(7), player), e -> unlockAbility(player, 7)));
						if (skill.getAbilityLevel(skillTree.getAbility(7)) > 0) {
							contents.set(track.get(10)/9, track.get(10)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(10), player), e -> unlockAbility(player, 10)));
							contents.set(track.get(11)/9, track.get(11)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(11), player), e -> unlockAbility(player, 11)));
						}
					}
				}
				if (skill.getAbilityLevel(skillTree.getAbility(3)) > 0) {
					contents.set(track.get(6)/9, track.get(6)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(6), player), e -> unlockAbility(player, 6)));
					if (skill.getAbilityLevel(skillTree.getAbility(6)) > 0) {
						contents.set(track.get(9)/9, track.get(9)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(9), player), e -> unlockAbility(player, 9)));
						if (skill.getAbilityLevel(skillTree.getAbility(9)) > 0) {
							contents.set(track.get(13)/9, track.get(13)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(13), player), e -> unlockAbility(player, 13)));
							contents.set(track.get(14)/9, track.get(14)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(14), player), e -> unlockAbility(player, 14)));
						}
					}
				}
				if (skill.getAbilityLevel(skillTree.getAbility(2)) > 0 && skill.getAbilityLevel(skillTree.getAbility(3)) > 0) {
					contents.set(track.get(5)/9, track.get(5)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(5), player), e -> unlockAbility(player, 5)));
					if (skill.getAbilityLevel(skillTree.getAbility(5)) > 0) {
						contents.set(track.get(8)/9, track.get(8)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(8), player), e -> unlockAbility(player, 8)));
						if (skill.getAbilityLevel(skillTree.getAbility(8)) > 0) {
							contents.set(track.get(12)/9, track.get(12)%9, ClickableItem.of(getUnlockAbility(skillTree.getAbility(12), player), e -> unlockAbility(player, 12)));
						}
					}
				}
			}
		}
	}
	
	public ItemStack getAbilityItem(Ability ability, Player player) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		ItemStack item = ability.getBaseItem().clone();
		ItemMeta meta = item.getItemMeta();
		NumberFormat nf = new DecimalFormat("##.##");
		if (ability != Ability.NULL) {
			meta.setDisplayName(ChatColor.GREEN + ability.getDisplayName() + " " + ChatColor.WHITE + RomanNumber.toRoman(skill.getAbilityLevel(ability)));
			List<String> lore = new LinkedList<String>();
			String fullDesc = ability.getDescription().replace("_%", ChatColor.AQUA + nf.format(ability.getValue(skill.getAbilityLevel(ability))) + "%" + ChatColor.GRAY);
			fullDesc = fullDesc.replace("__", ChatColor.AQUA + nf.format(ability.getValue(skill.getAbilityLevel(ability))) + ChatColor.GRAY);
			String[] splitDesc = fullDesc.replaceAll("(?:\\s*)(.{1,"+ 38 +"})(?:\\s+|\\s*$)", "$1\n").split("\n");
			for (String s : splitDesc) {
				lore.add(ChatColor.GRAY + s);
			}
			lore.add(" ");
			lore.add(ChatColor.GRAY + "Level: " + ChatColor.WHITE + skill.getAbilityLevel(ability));
			if (skill.getAbilityLevel(ability) < ability.getMaxLevel()) {
				lore.add(ChatColor.GRAY + "Next Level: ");
				String fullLevelDesc = ability.getDescription().replace("_%", nf.format(ability.getValue(skill.getAbilityLevel(ability))) + " ➜ " + ChatColor.AQUA + nf.format(ability.getValue(skill.getAbilityLevel(ability) + 1)) + "%" + ChatColor.GRAY);
				fullLevelDesc = fullLevelDesc.replace("__", nf.format(ability.getValue(skill.getAbilityLevel(ability))) + " ➜ " + ChatColor.AQUA + nf.format(ability.getValue(skill.getAbilityLevel(ability) + 1)) + ChatColor.GRAY);
				String[] splitLevelDesc = fullLevelDesc.replaceAll("(?:\\s*)(.{1,"+ 35 +"})(?:\\s+|\\s*$)", "$1\n").split("\n");
				for (String s : splitLevelDesc) {
					lore.add(ChatColor.GRAY + "   " + s);
				}
				lore.add(" ");
				lore.add(ChatColor.YELLOW + "Click to Upgrade!");
				lore.add(ChatColor.GRAY + "(You have " + skill.getSkillPoints(skillTree.getSkill()) + " Skill Points)");
				lore.add(ChatColor.GREEN + "COST " + ChatColor.GOLD + ability.getCostPerLevel() + " Skill Points");
				if (skill.getSkillPoints(skillTree.getSkill()) < ability.getCostPerLevel()) {
					lore.add(" ");
					lore.add(ChatColor.RED + "" + ChatColor.BOLD + "Too Expensive!");
				}
			}
			else {
				lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "MAXED");
			}
			meta.setLore(lore);
		}
		else {
			meta.setDisplayName(ChatColor.RED + "???");
			List<String> lore = new LinkedList<String>();
			lore.add(ChatColor.GRAY + "Coming soon...");
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack getUnlockAbility(Ability ability, Player player) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		ItemStack item;
		if (ability != Ability.NULL) {
			item = XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem();
			ItemMeta meta = item.getItemMeta();
			NumberFormat nf = new DecimalFormat("##.##");
			meta.setDisplayName(ChatColor.GREEN + ability.getDisplayName() + " " + ChatColor.WHITE + RomanNumber.toRoman(1));
			List<String> lore = new LinkedList<String>();
			String fullDesc = ability.getDescription().replace("_%", ChatColor.AQUA + nf.format(ability.getValue(1)) + "%" + ChatColor.GRAY);
			fullDesc = fullDesc.replace("__", ChatColor.AQUA + nf.format(ability.getValue(1)) + ChatColor.GRAY);
			String[] splitDesc = fullDesc.replaceAll("(?:\\s*)(.{1,"+ 38 +"})(?:\\s+|\\s*$)", "$1\n").split("\n");
			for (String s : splitDesc) {
				lore.add(ChatColor.GRAY + s);
			}
			lore.add(" ");
			lore.add(ChatColor.GRAY + "Max Level: " + ChatColor.WHITE + ability.getMaxLevel());
			lore.add(" ");
			lore.add(ChatColor.YELLOW + "Click to Unlock This Ability! ");
			lore.add(ChatColor.GRAY + "(You have " + skill.getSkillPoints(skillTree.getSkill()) + " Skill Points)");
			if (ability.getUnlockCost() == 1) {
				lore.add(ChatColor.GREEN + "COST " + ChatColor.GOLD + ability.getUnlockCost() + " Skill Point");
			} else {
				lore.add(ChatColor.GREEN + "COST " + ChatColor.GOLD + ability.getUnlockCost() + " Skill Points");
			}
			if (skill.getSkillPoints(skillTree.getSkill()) < ability.getUnlockCost()) {
				lore.add(" ");
				lore.add(ChatColor.RED + "" + ChatColor.BOLD + "Too Expensive!");
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		else {
			item = XMaterial.GRAY_DYE.parseItem();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "???");
			List<String> lore = new LinkedList<String>();
			lore.add(ChatColor.GRAY + "Coming soon...");
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}
	
	
	public ItemStack getLockedAbility() {
		ItemStack item = XMaterial.GRAY_DYE.parseItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "???");
		List<String> lore = new LinkedList<String>();
		lore.add(ChatColor.GRAY + "Unlock the abilities below to unlock this ability!");
		lore.add(" ");
		lore.add(ChatColor.RED + "" + ChatColor.BOLD + "LOCKED");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack getNullPane() {
		ItemStack item = XMaterial.GRAY_DYE.parseItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "???");
		List<String> lore = new LinkedList<String>();
		lore.add(ChatColor.GRAY + "Coming soon...");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
