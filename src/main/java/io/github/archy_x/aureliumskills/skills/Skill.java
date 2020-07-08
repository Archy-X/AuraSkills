package io.github.archy_x.aureliumskills.skills;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import io.github.archy_x.aureliumskills.Lang;
import io.github.archy_x.aureliumskills.Message;
import io.github.archy_x.aureliumskills.skills.levelers.Leveler;
import io.github.archy_x.aureliumskills.stats.Stat;
import io.github.archy_x.aureliumskills.util.RomanNumber;
import io.github.archy_x.aureliumskills.util.XMaterial;

public enum Skill {

	FARMING(Stat.HEALTH, Stat.STRENGTH, "Harvest crops to earn Farming XP", Material.DIAMOND_HOE),
	FORAGING(Stat.STRENGTH, Stat.TOUGHNESS, "Cut trees to earn Foraging XP", Material.STONE_AXE),
	MINING(Stat.TOUGHNESS, Stat.LUCK, "Mine stone and ores to earn Mining XP", Material.IRON_PICKAXE),
	FISHING(Stat.LUCK, Stat.HEALTH, "Catch fish to earn Fishing XP", Material.FISHING_ROD),
	EXCAVATION(Stat.REGENERATION, Stat.LUCK, "Dig with a shovel to earn Excavation XP", XMaterial.GOLDEN_SHOVEL.parseMaterial()),
	ARCHERY(Stat.LUCK, Stat.STRENGTH, "Shoot mobs and players with a bow to earn Archery XP", Material.BOW),
	DEFENSE(Stat.TOUGHNESS, Stat.HEALTH, "Take damage from entities to earn Defense XP", Material.CHAINMAIL_CHESTPLATE),
	FIGHTING(Stat.STRENGTH, Stat.REGENERATION, "Fight mobs with melee weapons to earn Fighting XP", Material.DIAMOND_SWORD),
	ENDURANCE(Stat.REGENERATION, Stat.TOUGHNESS, "Walk and run to earn Endurance XP", Material.GOLDEN_APPLE),
	AGILITY(Stat.WISDOM, Stat.REGENERATION, "Jump and take fall damage to earn Agility XP", Material.FEATHER),
	ALCHEMY(Stat.HEALTH, Stat.WISDOM, "Brew potions to earn Alchemy XP", XMaterial.POTION.parseMaterial()),
	ENCHANTING(Stat.WISDOM, Stat.LUCK, "Enchant items and books to earn Enchanting XP", XMaterial.ENCHANTING_TABLE.parseMaterial()),
	SORCERY(Stat.STRENGTH, Stat.WISDOM, "Cast spells to earn Sorcery XP", Material.BLAZE_ROD),
	HEALING(Stat.REGENERATION, Stat.HEALTH, "Drink and splash potions to earn Healing XP", Material.SPLASH_POTION),
	FORGING(Stat.TOUGHNESS, Stat.WISDOM, "Combine and apply books in an anvil to earn Forging XP", Material.ANVIL);
	
	private Stat primaryStat;
	private Stat secondaryStat;
	private String description;
	private String name;
	private Material material;
	
	private Skill(Stat primaryStat, Stat secondaryStat, String description, Material material) {
		this.name = this.toString().toLowerCase();
		this.primaryStat = primaryStat;
		this.secondaryStat = secondaryStat;
		this.description = description;
		this.material = material;
	}
	
	public ItemStack getMenuItem(Player player, boolean showClickText) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		int level = skill.getSkillLevel(this);
		double xp = skill.getXp(this);
		double xpToNext;
		if (Leveler.levelReqs.size() > level - 1) {
			xpToNext = Leveler.levelReqs.get(level - 1);
		}
		else {
			xpToNext = 0;
		}
		NumberFormat nf = new DecimalFormat("##.##");
		ItemStack item = new ItemStack(material);
		List<String> lore = new LinkedList<String>();
		String fullDesc = Lang.getMessage(Message.valueOf(this.toString().toUpperCase() + "_DESCRIPTION"));
		String[] splitDesc = fullDesc.replaceAll("(?:\\s*)(.{1,"+ 38 +"})(?:\\s+|\\s*$)", "$1\n").split("\n");
		for (String s : splitDesc) {
			lore.add(ChatColor.GRAY + s);
		}
		lore.add(" ");
		lore.add(ChatColor.GRAY + Lang.getMessage(Message.PRIMARY_STAT) + ": " + primaryStat.getColor() + Lang.getMessage(Message.valueOf(primaryStat.toString().toUpperCase() + "_NAME")));
		lore.add(ChatColor.GRAY + Lang.getMessage(Message.SECONDARY_STAT) + ": " + secondaryStat.getColor() + Lang.getMessage(Message.valueOf(secondaryStat.toString().toUpperCase() + "_NAME")));
		lore.add(" ");
		lore.add(ChatColor.GRAY + Lang.getMessage(Message.SKILL_POINTS_PLURAL) + ": " + ChatColor.YELLOW + skill.getSkillPoints(this));
		lore.add(ChatColor.GRAY + Lang.getMessage(Message.LEVEL) + ": " + ChatColor.YELLOW + RomanNumber.toRoman(level));
		if (xpToNext != 0) {
			lore.add(ChatColor.GRAY + Lang.getMessage(Message.PROGRESS_TO_LEVEL).replace("_", RomanNumber.toRoman(level + 1)) + ": " + ChatColor.YELLOW + nf.format(xp/xpToNext * 100) + "%");
			lore.add(ChatColor.GRAY + "   " + nf.format(xp) + "/" + (int) xpToNext + " XP");
		}
		else {
			lore.add(ChatColor.GOLD + Lang.getMessage(Message.MAX_LEVEL));
		}
		
		if (showClickText) {
			lore.add(" ");
			lore.add(ChatColor.YELLOW + Lang.getMessage(Message.LEFT_CLICK_SKILL));
			lore.add(ChatColor.YELLOW + Lang.getMessage(Message.RIGHT_CLICK_SKILL));
		}
		if (material.equals(Material.SPLASH_POTION) || material.equals(Material.POTION)) {
			PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
			potionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
			potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			potionMeta.setDisplayName(ChatColor.AQUA + Lang.getMessage(Message.valueOf(this.toString().toUpperCase() + "_NAME")) + ChatColor.DARK_AQUA + " " + RomanNumber.toRoman(level));
			potionMeta.setLore(lore);
			item.setItemMeta(potionMeta);
		}
		else {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.AQUA + Lang.getMessage(Message.valueOf(this.toString().toUpperCase() + "_NAME")) + ChatColor.DARK_AQUA + " " + RomanNumber.toRoman(level));
			meta.setLore(lore);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(meta);
		}
		return item;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getDisplayName() {
		return StringUtils.capitalize(name);
	}
	
	public String getName() {
		return name;
	}
	
	public Stat getPrimaryStat() {
		return primaryStat;
	}
	
	public Stat getSecondaryStat() {
		return secondaryStat;
	}
	
}
