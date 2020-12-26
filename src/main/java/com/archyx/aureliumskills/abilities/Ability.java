package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionValue;
import com.archyx.aureliumskills.lang.AbilityMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public enum Ability {
	
	BOUNTIFUL_HARVEST(() -> Skill.FARMING, 10.0, 5.0),
	FARMER(() -> Skill.FARMING, 10.0, 10.0),
	SCYTHE_MASTER(() -> Skill.FARMING, 3.0, 2.0),
	GENETICIST(() -> Skill.FARMING, 1.0, 2.0),
	TRIPLE_HARVEST(() -> Skill.FARMING, 5.0, 3.0),
	LUMBERJACK(() -> Skill.FORAGING, 10.0, 5.0),
	FORAGER(() -> Skill.FORAGING, 10.0, 10.0),
	AXE_MASTER(() -> Skill.FORAGING, 4.0, 3.0),
	VALOR(() -> Skill.FORAGING, 1.0, 1.0),
	SHREDDER(() -> Skill.FORAGING, 3.0, 3.0),
	LUCKY_MINER(() -> Skill.MINING, 3.0, 3.0),
	MINER(() -> Skill.MINING, 10.0, 10.0),
	PICK_MASTER(() -> Skill.MINING, 3.0, 2.0),
	STAMINA(() -> Skill.MINING, 1.0, 1.0),
	HARDENED_ARMOR(() -> Skill.MINING, 3.0, 3.0),
	LUCKY_CATCH(() -> Skill.FISHING, 10.0, 5.0),
	FISHER(() -> Skill.FISHING, 10.0, 10.0),
	TREASURE_HUNTER(() -> Skill.FISHING, 1.0, 1.0),
	GRAPPLER(() -> Skill.FISHING, 50.0, 100.0),
	EPIC_CATCH(() -> Skill.FISHING, 0.4, 0.4),
	METAL_DETECTOR(() -> Skill.EXCAVATION, 0.5, 0.4),
	EXCAVATOR(() -> Skill.EXCAVATION, 10.0, 10.0),
	SPADE_MASTER(() -> Skill.EXCAVATION, 3.0, 2.0),
	BIGGER_SCOOP(() -> Skill.EXCAVATION, 3.0, 3.0),
	LUCKY_SPADES(() -> Skill.EXCAVATION, 0.1, 0.05),
	CRIT_CHANCE(() -> Skill.ARCHERY, 5.0, 5.0),
	ARCHER(() -> Skill.ARCHERY, 10.0, 10.0),
	BOW_MASTER(() -> Skill.ARCHERY, 3.0, 2.0),
	PIERCING(() -> Skill.ARCHERY, 3.0, 3.0),
	STUN(() -> Skill.ARCHERY, 2.0, 1.0),
	SHIELDING(() -> Skill.DEFENSE, 2.0, 3.0),
	DEFENDER(() -> Skill.DEFENSE, 10.0, 10.0),
	MOB_MASTER(() -> Skill.DEFENSE, 2.0, 3.0),
	IMMUNITY(() -> Skill.DEFENSE, 0.5, 0.4),
	NO_DEBUFF(() -> Skill.DEFENSE, 5.0, 5.0),
	CRIT_DAMAGE(() -> Skill.FIGHTING, 5.0, 4.0),
	FIGHTER(() -> Skill.FIGHTING, 10.0, 10.0),
	SWORD_MASTER(() -> Skill.FIGHTING, 3.0, 2.0),
	FIRST_STRIKE(() -> Skill.FIGHTING, 20.0, 10.0),
	BLEED(() -> Skill.FIGHTING, 3.0, 4.0, 0.5, 0.5),
	ANTI_HUNGER(() -> Skill.ENDURANCE, 5.0, 5.0),
	RUNNER(() -> Skill.ENDURANCE, 10.0, 10.0),
	GOLDEN_HEAL(() -> Skill.ENDURANCE, 5.0, 6.0),
	RECOVERY(() -> Skill.ENDURANCE, 10, 10),
	MEAL_STEAL(() -> Skill.ENDURANCE, 1.0, 2.0),
	LIGHT_FALL(() -> Skill.AGILITY, 2.0, 1.5),
	JUMPER(() -> Skill.AGILITY, 10.0, 10.0),
	SUGAR_RUSH(() -> Skill.AGILITY, 5.0, 7.0),
	FLEETING(() -> Skill.AGILITY, 5.0, 4.0),
	THUNDER_FALL(() -> Skill.AGILITY, 3.0, 2.0, 10, 5),
	ALCHEMIST(() -> Skill.ALCHEMY, 3.0, 4.0, new String[] {"add_item_lore"}, new Object[] {true}),
	BREWER(() -> Skill.ALCHEMY, 10.0, 10.0),
	SPLASHER(() -> Skill.ALCHEMY, 0.5, 0.25),
	LINGERING(() -> Skill.ALCHEMY, 5.0, 4.0, 3.0, 2.0),
	WISE_EFFECT(() -> Skill.ALCHEMY, 1.0, 1.0),
	XP_CONVERT(() -> Skill.ENCHANTING, 20.0, -0.75),
	ENCHANTER(() -> Skill.ENCHANTING, 10.0, 10.0),
	XP_WARRIOR(() -> Skill.ENCHANTING, 5.0, 4.0),
	ENCHANTED_STRENGTH(() -> Skill.ENCHANTING, 0.5, 0.5),
	LUCKY_TABLE(() -> Skill.ENCHANTING, 5.0, 5.0),
	SORCERER(() -> Skill.SORCERY, 10.0, 10.0),
	HEALER(() -> Skill.HEALING, 10.0, 10.0),
	FORGER(() -> Skill.FORGING, 10.0, 10.0);
	
	private final double baseValue;
	private final double valuePerLevel;
	private boolean hasTwoValues;
	private double baseValue2;
	private double valuePerLevel2;
	private final Supplier<Skill> skill;
	private Map<String, OptionValue> options;
	
	Ability(Supplier<Skill> skill, double baseValue, double valuePerLevel) {
		this.baseValue = baseValue;
		this.valuePerLevel = valuePerLevel;
		this.skill = skill;
	}

	Ability(Supplier<Skill> skill, double baseValue, double valuePerLevel, String[] optionKeys, Object[] optionValues) {
		this.baseValue = baseValue;
		this.valuePerLevel = valuePerLevel;
		this.skill = skill;
		this.options = new HashMap<>();
		for (int i = 0; i < optionKeys.length; i++) {
			if (i < optionValues.length) {
				options.put(optionKeys[i], new OptionValue(optionValues[i]));
			}
		}
	}

	Ability(Supplier<Skill> skill, double baseValue1, double valuePerLevel1, double baseValue2, double valuePerLevel2) {
		this.skill = skill;
		this.hasTwoValues = true;
		this.baseValue = baseValue1;
		this.valuePerLevel = valuePerLevel1;
		this.baseValue2 = baseValue2;
		this.valuePerLevel2 = valuePerLevel2;
	}

	public String getInfo(Locale locale) {
		return Lang.getMessage(AbilityMessage.valueOf(this.name() + "_INFO"), locale);
	}
	
	public Skill getSkill() {
		return skill.get();
	}

	public double getValue(PlayerSkill playerSkill) {
		return getBaseValue() + (getValuePerLevel() * (playerSkill.getAbilityLevel(this) - 1));
	}

	public double getValue(int level) {
		return getBaseValue() + (getValuePerLevel() * (level - 1));
	}
	
	public double getBaseValue() {
		AbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
		if (option != null) {
			return option.getBaseValue();
		}
		return baseValue;
	}
	
	public double getValuePerLevel() {
		AbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
		if (option != null) {
			return option.getValuePerLevel();
		}
		return valuePerLevel;
	}

	public boolean hasTwoValues() {
		return hasTwoValues;
	}

	public double getValue2(int level) {
		return getBaseValue2() + (getValuePerLevel2() * (level - 1));
	}

	public double getBaseValue2() {
		AbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
		if (option != null) {
			return option.getBaseValue2();
		}
		return baseValue;
	}

	public double getValuePerLevel2() {
		AbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
		if (option != null) {
			return option.getValuePerLevel2();
		}
		return valuePerLevel;
	}

	public double getDefaultBaseValue() {
		return baseValue;
	}

	public double getDefaultValuePerLevel() {
		return valuePerLevel;
	}

	public double getDefaultBaseValue2() {
		return baseValue2;
	}

	public double getDefaultValuePerLevel2() {
		return valuePerLevel2;
	}

	public String getDisplayName(Locale locale) {
		return Lang.getMessage(AbilityMessage.valueOf(this.name() + "_NAME"), locale);
	}
	
	public String getDescription(Locale locale) {
		return Lang.getMessage(AbilityMessage.valueOf(this.name() + "_DESC"), locale);
	}

	public int getUnlock() {
		AbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
		if (option != null) {
			return option.getUnlock();
		}
		int defUnlock = 2;
		for (int i = 0; i < skill.get().getAbilities().size(); i++) {
			if (skill.get().getAbilities().get(i).get() == this) {
				defUnlock += i;
				break;
			}
		}
		return defUnlock;
	}

	public int getLevelUp() {
		AbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
		if (option != null) {
			return option.getLevelUp();
		}
		return 5;
	}

	public int getMaxLevel() {
		AbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
		if (option != null) {
			return option.getMaxLevel();
		}
		return 0;
	}

	/**
	 * Gets a list of abilities unlocked or leveled up at a certain level
	 * @param skill The skill
	 * @param level The skill level
	 * @return A list of abilities
	 */
	public static List<Ability> getAbilities(Skill skill, int level) {
		ImmutableList<Supplier<Ability>> skillAbilities = skill.getAbilities();
		List<Ability> abilities = new ArrayList<>();
		for (Supplier<Ability> abilitySupplier : skillAbilities) {
			Ability ability = abilitySupplier.get();
			if (level >= ability.getUnlock() && (level - ability.getUnlock()) % ability.getLevelUp() == 0) {
				abilities.add(ability);
			}
		}
		return abilities;
	}

	@Nullable
	public OptionValue getOption(String key) {
		AbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
		if (option != null) {
			return option.getOption(key);
		} else {
			return this.options.get(key);
		}
	}

	public boolean getOptionAsBooleanElseTrue(String key) {
		OptionValue value = getOption(key);
		if (value != null) {
			return value.asBoolean();
		}
		return true;
	}

	@Nullable
	public Set<String> getOptionKeys() {
		if (options != null) {
			return options.keySet();
		}
		return null;
	}

}
