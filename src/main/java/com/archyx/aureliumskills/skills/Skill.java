package com.archyx.aureliumskills.skills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.RomanNumber;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public enum Skill {

	FARMING(Stat.HEALTH, Stat.STRENGTH, "Harvest crops to earn Farming XP", Material.DIAMOND_HOE, 
			new Ability[] {Ability.BOUNTIFUL_HARVEST, Ability.FARMER, Ability.SCYTHE_MASTER, Ability.GENETICIST, Ability.TRIPLE_HARVEST},
			MAbility.REPLENISH),
	FORAGING(Stat.STRENGTH, Stat.TOUGHNESS, "Cut trees to earn Foraging XP", Material.STONE_AXE, 
			new Ability[] {Ability.LUMBERJACK, Ability.FORAGER, Ability.AXE_MASTER, Ability.VALOR, Ability.SHREDDER},
			MAbility.TREECAPITATOR),
	MINING(Stat.TOUGHNESS, Stat.LUCK, "Mine stone and ores to earn Mining XP", Material.IRON_PICKAXE, 
			new Ability[] {Ability.LUCKY_MINER, Ability.MINER, Ability.PICK_MASTER, Ability.STAMINA, Ability.HARDENED_ARMOR},
			MAbility.SPEED_MINE),
	FISHING(Stat.LUCK, Stat.HEALTH, "Catch fish to earn Fishing XP", Material.FISHING_ROD, 
			new Ability[] {Ability.LUCKY_CATCH, Ability.FISHER, Ability.TREASURE_HUNTER, Ability.GRAPPLER, Ability.EPIC_CATCH},
			MAbility.ABSORPTION),
	EXCAVATION(Stat.REGENERATION, Stat.LUCK, "Dig with a shovel to earn Excavation XP", XMaterial.GOLDEN_SHOVEL.parseMaterial(),
			new Ability[] {Ability.METAL_DETECTOR, Ability.EXCAVATOR, Ability.SPADE_MASTER, Ability.BIGGER_SCOOP, Ability.LUCKY_SPADES},
			MAbility.ABSORPTION),
	ARCHERY(Stat.LUCK, Stat.STRENGTH, "Shoot mobs and players with a bow to earn Archery XP", Material.BOW,
			new Ability[] {Ability.CRIT_CHANCE, Ability.ARCHER, Ability.BOW_MASTER, Ability.PIERCING, Ability.STUN},
			MAbility.ABSORPTION),
	DEFENSE(Stat.TOUGHNESS, Stat.HEALTH, "Take damage from entities to earn Defense XP", Material.CHAINMAIL_CHESTPLATE,
			new Ability[] {Ability.SHIELDING, Ability.DEFENDER, Ability.MOB_MASTER, Ability.IMMUNITY, Ability.NO_DEBUFF},
			MAbility.ABSORPTION),
	FIGHTING(Stat.STRENGTH, Stat.REGENERATION, "Fight mobs with melee weapons to earn Fighting XP", Material.DIAMOND_SWORD,
			new Ability[] {Ability.CRIT_DAMAGE, Ability.FIGHTER, Ability.SWORD_MASTER, Ability.FIRST_STRIKE, Ability.BLEED},
			MAbility.ABSORPTION),
	ENDURANCE(Stat.REGENERATION, Stat.TOUGHNESS, "Walk and run to earn Endurance XP", Material.GOLDEN_APPLE,
			new Ability[] {Ability.ANTI_HUNGER, Ability.RUNNER, Ability.GOLDEN_HEAL, Ability.RECOVERY, Ability.MEAL_STEAL},
			MAbility.ABSORPTION),
	AGILITY(Stat.WISDOM, Stat.REGENERATION, "Jump and take fall damage to earn Agility XP", Material.FEATHER,
			new Ability[] {Ability.JUMPER},
			MAbility.ABSORPTION),
	ALCHEMY(Stat.HEALTH, Stat.WISDOM, "Brew potions to earn Alchemy XP", XMaterial.POTION.parseMaterial(),
			new Ability[] {Ability.BREWER},
			MAbility.ABSORPTION),
	ENCHANTING(Stat.WISDOM, Stat.LUCK, "Enchant items and books to earn Enchanting XP", XMaterial.ENCHANTING_TABLE.parseMaterial(),
			new Ability[] {Ability.ENCHANTER},
			MAbility.ABSORPTION),
	SORCERY(Stat.STRENGTH, Stat.WISDOM, "Cast spells to earn Sorcery XP", Material.BLAZE_ROD,
			new Ability[] {Ability.SORCERER},
			MAbility.ABSORPTION),
	HEALING(Stat.REGENERATION, Stat.HEALTH, "Drink and splash potions to earn Healing XP", Material.SPLASH_POTION,
			new Ability[] {Ability.HEALER},
			MAbility.ABSORPTION),
	FORGING(Stat.TOUGHNESS, Stat.WISDOM, "Combine and apply books in an anvil to earn Forging XP", Material.ANVIL,
			new Ability[] {Ability.FORGER},
			MAbility.ABSORPTION);
	
	private final Stat primaryStat;
	private final Stat secondaryStat;
	private final String description;
	private final String name;
	private final Material material;
	private final Ability[] abilities;
	private final MAbility manaAbility;
	private final NumberFormat nf = new DecimalFormat("#.#");
	
	Skill(Stat primaryStat, Stat secondaryStat, String description, Material material, Ability[] abilities, MAbility manaAbility) {
		this.name = this.toString().toLowerCase();
		this.primaryStat = primaryStat;
		this.secondaryStat = secondaryStat;
		this.description = description;
		this.material = material;
		this.abilities = abilities;
		this.manaAbility = manaAbility;
	}
	
	public ItemStack getMenuItem(Player player, boolean showClickText) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		int level = skill.getSkillLevel(this);
		double xp = skill.getXp(this);
		double xpToNext;
		if (Leveler.levelReqs.size() > level - 1 && level < OptionL.getMaxLevel(this)) {
			xpToNext = Leveler.levelReqs.get(level - 1);
		} else {
			xpToNext = 0;
		}
		NumberFormat nf = new DecimalFormat("##.##");
		ItemStack item = new ItemStack(material);
		List<String> lore = new LinkedList<>();
		String fullDesc = Lang.getMessage(Message.valueOf(this.toString().toUpperCase() + "_DESCRIPTION"));
		if (fullDesc != null) {
			String[] splitDesc = fullDesc.replaceAll("(?:\\s*)(.{1," + 38 + "})(?:\\s+|\\s*$)", "$1\n").split("\n");
			for (String s : splitDesc) {
				lore.add(ChatColor.GRAY + s);
			}
		}
		if (player.hasPermission("aureliumskills." + this.toString().toLowerCase())) {
			lore.add(" ");
			lore.add(ChatColor.GRAY + Lang.getMessage(Message.PRIMARY_STAT) + ": " + primaryStat.getColor() + Lang.getMessage(Message.valueOf(primaryStat.toString().toUpperCase() + "_NAME")));
			lore.add(ChatColor.GRAY + Lang.getMessage(Message.SECONDARY_STAT) + ": " + secondaryStat.getColor() + Lang.getMessage(Message.valueOf(secondaryStat.toString().toUpperCase() + "_NAME")));
			//Ability Levels
			if (abilities.length == 5) {
				boolean hasSkills = false;
				for (Ability ability : this.getAbilities()) {
					if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
						hasSkills = true;
						break;
					}
				}
				if (hasSkills) {
					lore.add(" ");
					lore.add(ChatColor.GRAY + Lang.getMessage(Message.ABILITY_LEVELS) + ":");
					for (Ability ability : this.getAbilities()) {
						if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
							if (skill.getAbilityLevel(ability) > 0) {
								String miniDesc = ability.getMiniDescription().replace("_", nf.format(ability.getValue(skill.getAbilityLevel(ability))));
								if (ability.hasTwoValues()) {
									miniDesc = miniDesc.replace("$", nf.format(ability.getValue2(skill.getAbilityLevel(ability))));
								}
								lore.add(ChatColor.GOLD + "   " + ability.getDisplayName() + " " + RomanNumber.toRoman(skill.getAbilityLevel(ability)) + ChatColor.DARK_GRAY + " (" + miniDesc + ")");
							} else {
								lore.add(ChatColor.DARK_GRAY + "   " + ChatColor.STRIKETHROUGH + ability.getDisplayName());
							}
						}
					}
				}
			}
			//Mana ability
			if (skill.getManaAbilityLevel(manaAbility) > 0) {
				lore.add(" ");
				int manaAbilityLevel = skill.getManaAbilityLevel(manaAbility);
				lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Mana Ability " + ChatColor.BLUE + manaAbility.getName() + " " + RomanNumber.toRoman(manaAbilityLevel));
				lore.add(ChatColor.GRAY + "  Duration: " + ChatColor.GREEN + nf.format(manaAbility.getValue(manaAbilityLevel)) + "s" + ChatColor.GRAY + " Mana Cost: " + ChatColor.AQUA + manaAbility.getManaCost(manaAbilityLevel) + ChatColor.GRAY + " Cooldown: " + ChatColor.YELLOW + manaAbility.getCooldown(manaAbilityLevel) + "s");
			}
			//Level Progress
			lore.add(" ");
			lore.add(ChatColor.GRAY + Lang.getMessage(Message.LEVEL) + ": " + ChatColor.YELLOW + RomanNumber.toRoman(level));
			if (xpToNext != 0) {
				String progressToLevel = Lang.getMessage(Message.PROGRESS_TO_LEVEL);
				if (progressToLevel != null) {
					lore.add(ChatColor.GRAY + progressToLevel.replace("_", RomanNumber.toRoman(level + 1)) + ": " + ChatColor.YELLOW + nf.format(xp / xpToNext * 100) + "%");
				}
				lore.add(ChatColor.GRAY + "   " + nf.format(xp) + "/" + (int) xpToNext + " XP");
			} else {
				lore.add(ChatColor.GOLD + Lang.getMessage(Message.MAX_LEVEL));
			}
			//Click text
			if (showClickText) {
				lore.add(" ");
				lore.add(ChatColor.YELLOW + Lang.getMessage(Message.CLICK_SKILL));
			}
		} else {
			lore.add(" ");
			lore.add(ChatColor.RED + Lang.getMessage(Message.SKILL_LOCKED));
		}
		//Sets item
		if (material.equals(Material.SPLASH_POTION) || material.equals(Material.POTION)) {
			PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
			if (potionMeta != null) {
				potionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
				potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
				potionMeta.setDisplayName(ChatColor.AQUA + Lang.getMessage(Message.valueOf(this.toString().toUpperCase() + "_NAME")) + ChatColor.DARK_AQUA + " " + RomanNumber.toRoman(level));
				potionMeta.setLore(lore);
				item.setItemMeta(potionMeta);
			}
		} else {
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.AQUA + Lang.getMessage(Message.valueOf(this.toString().toUpperCase() + "_NAME")) + ChatColor.DARK_AQUA + " " + RomanNumber.toRoman(level));
				meta.setLore(lore);
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				item.setItemMeta(meta);
			}
		}
		return item;
	}
	
	public Ability[] getAbilities() {
		return abilities;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getDisplayName() {
		return Lang.getMessage(Message.valueOf(name.toUpperCase() + "_NAME"));
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

	public MAbility getManaAbility() {
		return manaAbility;
	}

	public static List<Skill> getOrderedValues() {
		List<Skill> list = new ArrayList<>();
		list.add(Skill.AGILITY);
		list.add(Skill.ALCHEMY);
		list.add(Skill.ARCHERY);
		list.add(Skill.DEFENSE);
		list.add(Skill.ENCHANTING);
		list.add(Skill.ENDURANCE);
		list.add(Skill.EXCAVATION);
		list.add(Skill.FARMING);
		list.add(Skill.FIGHTING);
		list.add(Skill.FISHING);
		list.add(Skill.FORAGING);
		list.add(Skill.FORGING);
		list.add(Skill.HEALING);
		list.add(Skill.MINING);
		list.add(Skill.SORCERY);
		return list;
	}
	
}
