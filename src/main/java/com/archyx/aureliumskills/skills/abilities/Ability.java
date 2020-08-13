package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.skills.Skill;

public enum Ability {
	
	BOUNTIFUL_HARVEST(Skill.FARMING, 10.0, 5.0),
	FARMER(Skill.FARMING, 10.0, 10.0),
	SCYTHE_MASTER(Skill.FARMING, 3.0, 2.0),
	GENETICIST(Skill.FARMING, 1.0, 2.0),
	TRIPLE_HARVEST(Skill.FARMING, 5.0, 3.0),
	LUMBERJACK(Skill.FORAGING, 10.0, 5.0),
	FORAGER(Skill.FORAGING, 10.0, 10.0),
	AXE_MASTER(Skill.FORAGING, 3.0, 2.0),
	TREECAPITATOR(Skill.FORAGING, 5.0, 5.0),
	SHREDDER(Skill.FORAGING, 3.0, 3.0),
	LUCKY_MINER(Skill.MINING, 3.0, 3.0),
	MINER(Skill.MINING, 10.0, 10.0),
	PICK_MASTER(Skill.MINING, 3.0, 2.0),
	SPEED_MINE(Skill.MINING, 5.0, 5.0),
	HARDENED_ARMOR(Skill.MINING, 3.0, 3.0),
	LUCKY_CATCH(Skill.FISHING, 10.0, 5.0),
	FISHER(Skill.FISHING, 10.0, 10.0),
	TREASURE_HUNTER(Skill.FISHING, 1.0, 1.0),
	GRAPPLER(Skill.FISHING, 50.0, 100.0),
	EPIC_CATCH(Skill.FISHING, 0.4, 0.4),
	METAL_DETECTOR(Skill.EXCAVATION, 1.0, 0.5),
	EXCAVATOR(Skill.EXCAVATION, 10.0, 10.0),
	SPADE_MASTER(Skill.EXCAVATION, 3.0, 2.0),
	BIGGER_SCOOP(Skill.EXCAVATION, 3.0, 3.0),
	LUCKY_SPADES(Skill.EXCAVATION, 0.1, 0.1),
	CRIT_CHANCE(Skill.ARCHERY, 5.0, 5.0),
	ARCHER(Skill.ARCHERY, 10.0, 10.0),
	BOW_MASTER(Skill.ARCHERY, 3.0, 2.0),
	PIERCING(Skill.ARCHERY, 3.0, 3.0),
	STUN(Skill.ARCHERY, 2.0, 1.0),
	SHIELDING(Skill.DEFENSE, 2.0, 3.0),
	DEFENDER(Skill.DEFENSE, 10.0, 10.0),
	MOB_MASTER(Skill.DEFENSE, 3.0, 4.0),
	IMMUNITY(Skill.DEFENSE, 0.5, 0.5),
	NO_DEBUFF(Skill.DEFENSE, 5.0, 5.0),
	CRIT_DAMAGE(Skill.FIGHTING, 5.0, 5.0),
	FIGHTER(Skill.FIGHTING, 10.0, 10.0),
	SWORD_MASTER(Skill.FIGHTING, 3.0, 2.0),
	FIRST_STRIKE(Skill.FIGHTING, 20.0, 10.0),
	BLEED(Skill.FIGHTING, 1.0, 1.0, 0.5, 0.5),
	RUNNER(Skill.ENDURANCE, 10.0, 10.0),
	JUMPER(Skill.AGILITY, 10.0, 10.0),
	BREWER(Skill.ALCHEMY, 10.0, 10.0),
	ENCHANTER(Skill.ENCHANTING, 10.0, 10.0),
	SORCERER(Skill.SORCERY, 10.0, 10.0),
	HEALER(Skill.HEALING, 10.0, 10.0),
	FORGER(Skill.FORGING, 10.0, 10.0);
	
	private final double baseValue;
	private final double valuePerLevel;
	private boolean hasTwoValues;
	private double baseValue2;
	private double valuePerLevel2;
	private final Skill skill;
	
	Ability(Skill skill, double baseValue, double valuePerLevel) {
		this.baseValue = baseValue;
		this.valuePerLevel = valuePerLevel;
		this.skill = skill;
	}

	Ability(Skill skill, double baseValue1, double valuePerLevel1, double baseValue2, double valuePerLevel2) {
		this.skill = skill;
		this.hasTwoValues = true;
		this.baseValue = baseValue1;
		this.valuePerLevel = valuePerLevel1;
		this.baseValue2 = baseValue2;
		this.valuePerLevel2 = valuePerLevel2;
	}

	public String getMiniDescription() {
		return Lang.getMessage(Message.valueOf(this.name() + "_MINI_DESC"));
	}
	
	public Skill getSkill() {
		return skill;
	}
	
	public double getValue(int level) {
		AbilityOptionManager option = AureliumSkills.abilityOptionManager;
		if (option.containsOption(this)) {
			AbilityOption abilityOption = option.getAbilityOption(this);
			return abilityOption.getBaseValue() + (abilityOption.getValuePerLevel() * (level - 1));
		}
		return baseValue + (valuePerLevel * (level - 1));
	}
	
	public double getBaseValue() {
		AbilityOptionManager option = AureliumSkills.abilityOptionManager;
		if (option.containsOption(this)) {
			return option.getAbilityOption(this).getBaseValue();
		}
		return baseValue;
	}
	
	public double getValuePerLevel() {
		AbilityOptionManager option = AureliumSkills.abilityOptionManager;
		if (option.containsOption(this)) {
			return option.getAbilityOption(this).getValuePerLevel();
		}
		return valuePerLevel;
	}

	public boolean hasTwoValues() {
		return hasTwoValues;
	}

	public double getValue2(int level) {
		AbilityOptionManager option = AureliumSkills.abilityOptionManager;
		if (option.containsOption(this)) {
			AbilityOption abilityOption = option.getAbilityOption(this);
			return abilityOption.getBaseValue2() + (abilityOption.getValuePerLevel2() * (level - 1));
		}
		return baseValue2 + (valuePerLevel2 * (level - 1));
	}

	public double getBaseValue2() {
		AbilityOptionManager option = AureliumSkills.abilityOptionManager;
		if (option.containsOption(this)) {
			return option.getAbilityOption(this).getBaseValue2();
		}
		return baseValue;
	}

	public double getValuePerLevel2() {
		AbilityOptionManager option = AureliumSkills.abilityOptionManager;
		if (option.containsOption(this)) {
			return option.getAbilityOption(this).getValuePerLevel2();
		}
		return valuePerLevel;
	}

	public String getDisplayName() {
		return Lang.getMessage(Message.valueOf(this.name() + "_NAME"));
	}
	
	public String getDescription() {
		return Lang.getMessage(Message.valueOf(this.name() + "_DESC"));
	}
}
