package com.archyx.aureliumskills.skills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.lang.SkillMessage;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.RomanNumber;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableList;
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
import java.util.function.Supplier;

public enum Skill {

	FARMING(Stat.HEALTH, Stat.STRENGTH, Material.DIAMOND_HOE,
			ImmutableList.of(() -> Ability.BOUNTIFUL_HARVEST, () -> Ability.FARMER, () -> Ability.SCYTHE_MASTER, () -> Ability.GENETICIST, () -> Ability.TRIPLE_HARVEST),
			MAbility.REPLENISH),
	FORAGING(Stat.STRENGTH, Stat.TOUGHNESS, Material.STONE_AXE,
			ImmutableList.of(() -> Ability.LUMBERJACK, () -> Ability.FORAGER, () -> Ability.AXE_MASTER, () -> Ability.VALOR, () -> Ability.SHREDDER),
			MAbility.TREECAPITATOR),
	MINING(Stat.TOUGHNESS, Stat.LUCK, Material.IRON_PICKAXE,
			ImmutableList.of(() -> Ability.LUCKY_MINER, () -> Ability.MINER, () -> Ability.PICK_MASTER, () -> Ability.STAMINA, () -> Ability.HARDENED_ARMOR),
			MAbility.SPEED_MINE),
	FISHING(Stat.LUCK, Stat.HEALTH, Material.FISHING_ROD,
			ImmutableList.of(() -> Ability.LUCKY_CATCH, () -> Ability.FISHER, () -> Ability.TREASURE_HUNTER, () -> Ability.GRAPPLER, () -> Ability.EPIC_CATCH),
			MAbility.ABSORPTION),
	EXCAVATION(Stat.REGENERATION, Stat.LUCK, XMaterial.GOLDEN_SHOVEL.parseMaterial(),
			ImmutableList.of(() -> Ability.METAL_DETECTOR, () -> Ability.EXCAVATOR, () -> Ability.SPADE_MASTER, () -> Ability.BIGGER_SCOOP, () -> Ability.LUCKY_SPADES),
			MAbility.ABSORPTION),
	ARCHERY(Stat.LUCK, Stat.STRENGTH, Material.BOW,
			ImmutableList.of(() -> Ability.CRIT_CHANCE, () -> Ability.ARCHER, () -> Ability.BOW_MASTER, () -> Ability.PIERCING, () -> Ability.STUN),
			MAbility.ABSORPTION),
	DEFENSE(Stat.TOUGHNESS, Stat.HEALTH, Material.CHAINMAIL_CHESTPLATE,
			ImmutableList.of(() -> Ability.SHIELDING, () -> Ability.DEFENDER, () -> Ability.MOB_MASTER, () -> Ability.IMMUNITY, () -> Ability.NO_DEBUFF),
			MAbility.ABSORPTION),
	FIGHTING(Stat.STRENGTH, Stat.REGENERATION, Material.DIAMOND_SWORD,
			ImmutableList.of(() -> Ability.CRIT_DAMAGE, () -> Ability.FIGHTER, () -> Ability.SWORD_MASTER, () -> Ability.FIRST_STRIKE, () -> Ability.BLEED),
			MAbility.ABSORPTION),
	ENDURANCE(Stat.REGENERATION, Stat.TOUGHNESS, Material.GOLDEN_APPLE,
			ImmutableList.of(() -> Ability.ANTI_HUNGER, () -> Ability.RUNNER, () -> Ability.GOLDEN_HEAL, () -> Ability.RECOVERY, () -> Ability.MEAL_STEAL),
			MAbility.ABSORPTION),
	AGILITY(Stat.WISDOM, Stat.REGENERATION, Material.FEATHER,
			ImmutableList.of(() -> Ability.LIGHT_FALL, () -> Ability.JUMPER, () -> Ability.SUGAR_RUSH, () -> Ability.FLEETING, () -> Ability.THUNDER_FALL),
			MAbility.ABSORPTION),
	ALCHEMY(Stat.HEALTH, Stat.WISDOM, XMaterial.POTION.parseMaterial(),
			ImmutableList.of(() -> Ability.ALCHEMIST, () -> Ability.BREWER, () -> Ability.SPLASHER, () -> Ability.LINGERING, () -> Ability.WISE_EFFECT),
			MAbility.ABSORPTION),
	ENCHANTING(Stat.WISDOM, Stat.LUCK, XMaterial.ENCHANTING_TABLE.parseMaterial(), ImmutableList.of(() -> Ability.ENCHANTER),
			MAbility.ABSORPTION),
	SORCERY(Stat.STRENGTH, Stat.WISDOM, Material.BLAZE_ROD, ImmutableList.of(() -> Ability.SORCERER),
			MAbility.ABSORPTION),
	HEALING(Stat.REGENERATION, Stat.HEALTH, Material.SPLASH_POTION, ImmutableList.of(() -> Ability.HEALER),
			MAbility.ABSORPTION),
	FORGING(Stat.TOUGHNESS, Stat.WISDOM, Material.ANVIL, ImmutableList.of(() -> Ability.FORGER),
			MAbility.ABSORPTION);
	
	private final Stat primaryStat;
	private final Stat secondaryStat;
	private final Material material;
	private final ImmutableList<Supplier<Ability>> abilities;
	private final MAbility manaAbility;
	
	Skill(Stat primaryStat, Stat secondaryStat, Material material, ImmutableList<Supplier<Ability>> abilities, MAbility manaAbility) {
		this.primaryStat = primaryStat;
		this.secondaryStat = secondaryStat;
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
		lore.add(getDescription());
		if (player.hasPermission("aureliumskills." + this.toString().toLowerCase())) {
			lore.add(" ");
			lore.add(Lang.getMessage(MenuMessage.PRIMARY_STAT).replace("{color}", primaryStat.getColor()).replace("{stat}", primaryStat.getDisplayName()));
			lore.add(Lang.getMessage(MenuMessage.SECONDARY_STAT).replace("{color}", secondaryStat.getColor()).replace("{stat}", secondaryStat.getDisplayName()));
			//Ability Levels
			if (abilities.size() == 5) {
				boolean hasSkills = false;
				for (Supplier<Ability> supplier : this.getAbilities()) {
					Ability ability = supplier.get();
					if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
						hasSkills = true;
						break;
					}
				}
				if (hasSkills) {
					lore.add(" ");
					String abilityLevels = Lang.getMessage(MenuMessage.ABILITY_LEVELS);
					int count = 1;
					//Replace message with contexts
					for (Supplier<Ability> supplier : abilities) {
						Ability ability = supplier.get();
						if (ability != null) {
							if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
								if (skill.getAbilityLevel(ability) > 0) {
									abilityLevels = abilityLevels.replace("{ability_" + count + "}", Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY)
											.replace("{ability}", ability.getDisplayName())
											.replace("{level}", RomanNumber.toRoman(skill.getAbilityLevel(ability)))
											.replace("{info}", ability.getMiniDescription()
													.replace("{value}", nf.format(ability.getValue(skill.getAbilityLevel(ability))))
													.replace("{value_2}", nf.format(ability.getValue2(skill.getAbilityLevel(ability))))));
								} else {
									abilityLevels = abilityLevels.replace("{ability_" + count + "}", Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY_LOCKED)
											.replace("{ability}", ability.getDisplayName()));
								}
								count++;
							}
						}
					}
					lore.add(abilityLevels);
				}
			}
			//Mana ability
			if (skill.getManaAbilityLevel(manaAbility) > 0) {
				lore.add(" ");
				int manaAbilityLevel = skill.getManaAbilityLevel(manaAbility);
				lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Mana Ability " + ChatColor.BLUE + manaAbility.getDisplayName() + " " + RomanNumber.toRoman(manaAbilityLevel));
				lore.add(ChatColor.GRAY + "  Duration: " + ChatColor.GREEN + nf.format(manaAbility.getValue(manaAbilityLevel)) + "s" + ChatColor.GRAY + " Mana Cost: " + ChatColor.AQUA + manaAbility.getManaCost(manaAbilityLevel) + ChatColor.GRAY + " Cooldown: " + ChatColor.YELLOW + manaAbility.getCooldown(manaAbilityLevel) + "s");
			}
			//Level Progress
			lore.add(" ");
			lore.add(ChatColor.GRAY + Lang.getMessage(MenuMessage.LEVEL).replace("{level}", RomanNumber.toRoman(level)));
			if (xpToNext != 0) {
				lore.add(Lang.getMessage(MenuMessage.PROGRESS_TO_LEVEL)
						.replace("{level}", RomanNumber.toRoman(level + 1))
						.replace("{percent}", nf.format(xp / xpToNext * 100))
						.replace("{current_xp}", nf.format(xp))
						.replace("{level_xp}", String.valueOf((int) xpToNext)));
			} else {
				lore.add(ChatColor.GOLD + Lang.getMessage(MenuMessage.MAX_LEVEL));
			}
			//Click text
			if (showClickText) {
				lore.add(" ");
				lore.add(Lang.getMessage(MenuMessage.SKILL_CLICK));
			}
		} else {
			lore.add(" ");
			lore.add(Lang.getMessage(MenuMessage.SKILL_LOCKED));
		}
		//Sets item
		if (material.equals(Material.SPLASH_POTION) || material.equals(Material.POTION)) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			if (meta != null) {
				meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
				meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
				meta.setDisplayName(ChatColor.AQUA + Lang.getMessage(SkillMessage.valueOf(this.toString().toUpperCase() + "_NAME")) + ChatColor.DARK_AQUA + " " + RomanNumber.toRoman(level));
				meta.setLore(ItemUtils.formatLore(lore));
				item.setItemMeta(meta);
			}
		} else {
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.AQUA + Lang.getMessage(SkillMessage.valueOf(this.toString().toUpperCase() + "_NAME")) + ChatColor.DARK_AQUA + " " + RomanNumber.toRoman(level));
				meta.setLore(ItemUtils.formatLore(lore));
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				item.setItemMeta(meta);
			}
		}
		return item;
	}

	public ImmutableList<Supplier<Ability>> getAbilities() {
		return abilities;
	}
	
	public String getDescription() {
		return Lang.getMessage(SkillMessage.valueOf(this.name() + "_DESC"));
	}
	
	public String getDisplayName() {
		return Lang.getMessage(SkillMessage.valueOf(this.name().toUpperCase() + "_NAME"));
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
