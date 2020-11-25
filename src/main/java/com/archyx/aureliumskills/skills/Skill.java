package com.archyx.aureliumskills.skills;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.SkillMessage;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.stats.Stat;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public enum Skill {

	FARMING(Stat.HEALTH, Stat.STRENGTH,
			ImmutableList.of(() -> Ability.BOUNTIFUL_HARVEST, () -> Ability.FARMER, () -> Ability.SCYTHE_MASTER, () -> Ability.GENETICIST, () -> Ability.TRIPLE_HARVEST),
			MAbility.REPLENISH),
	FORAGING(Stat.STRENGTH, Stat.TOUGHNESS,
			ImmutableList.of(() -> Ability.LUMBERJACK, () -> Ability.FORAGER, () -> Ability.AXE_MASTER, () -> Ability.VALOR, () -> Ability.SHREDDER),
			MAbility.TREECAPITATOR),
	MINING(Stat.TOUGHNESS, Stat.LUCK,
			ImmutableList.of(() -> Ability.LUCKY_MINER, () -> Ability.MINER, () -> Ability.PICK_MASTER, () -> Ability.STAMINA, () -> Ability.HARDENED_ARMOR),
			MAbility.SPEED_MINE),
	FISHING(Stat.LUCK, Stat.HEALTH,
			ImmutableList.of(() -> Ability.LUCKY_CATCH, () -> Ability.FISHER, () -> Ability.TREASURE_HUNTER, () -> Ability.GRAPPLER, () -> Ability.EPIC_CATCH),
			MAbility.ABSORPTION),
	EXCAVATION(Stat.REGENERATION, Stat.LUCK,
			ImmutableList.of(() -> Ability.METAL_DETECTOR, () -> Ability.EXCAVATOR, () -> Ability.SPADE_MASTER, () -> Ability.BIGGER_SCOOP, () -> Ability.LUCKY_SPADES),
			MAbility.ABSORPTION),
	ARCHERY(Stat.LUCK, Stat.STRENGTH,
			ImmutableList.of(() -> Ability.CRIT_CHANCE, () -> Ability.ARCHER, () -> Ability.BOW_MASTER, () -> Ability.PIERCING, () -> Ability.STUN),
			MAbility.ABSORPTION),
	DEFENSE(Stat.TOUGHNESS, Stat.HEALTH,
			ImmutableList.of(() -> Ability.SHIELDING, () -> Ability.DEFENDER, () -> Ability.MOB_MASTER, () -> Ability.IMMUNITY, () -> Ability.NO_DEBUFF),
			MAbility.ABSORPTION),
	FIGHTING(Stat.STRENGTH, Stat.REGENERATION,
			ImmutableList.of(() -> Ability.CRIT_DAMAGE, () -> Ability.FIGHTER, () -> Ability.SWORD_MASTER, () -> Ability.FIRST_STRIKE, () -> Ability.BLEED),
			MAbility.ABSORPTION),
	ENDURANCE(Stat.REGENERATION, Stat.TOUGHNESS,
			ImmutableList.of(() -> Ability.ANTI_HUNGER, () -> Ability.RUNNER, () -> Ability.GOLDEN_HEAL, () -> Ability.RECOVERY, () -> Ability.MEAL_STEAL),
			MAbility.ABSORPTION),
	AGILITY(Stat.WISDOM, Stat.REGENERATION,
			ImmutableList.of(() -> Ability.LIGHT_FALL, () -> Ability.JUMPER, () -> Ability.SUGAR_RUSH, () -> Ability.FLEETING, () -> Ability.THUNDER_FALL),
			MAbility.ABSORPTION),
	ALCHEMY(Stat.HEALTH, Stat.WISDOM,
			ImmutableList.of(() -> Ability.ALCHEMIST, () -> Ability.BREWER, () -> Ability.SPLASHER, () -> Ability.LINGERING, () -> Ability.WISE_EFFECT),
			MAbility.ABSORPTION),
	ENCHANTING(Stat.WISDOM, Stat.LUCK, ImmutableList.of(() -> Ability.ENCHANTER),
			MAbility.ABSORPTION),
	SORCERY(Stat.STRENGTH, Stat.WISDOM, ImmutableList.of(() -> Ability.SORCERER),
			MAbility.ABSORPTION),
	HEALING(Stat.REGENERATION, Stat.HEALTH, ImmutableList.of(() -> Ability.HEALER),
			MAbility.ABSORPTION),
	FORGING(Stat.TOUGHNESS, Stat.WISDOM, ImmutableList.of(() -> Ability.FORGER),
			MAbility.ABSORPTION);
	
	private final Stat primaryStat;
	private final Stat secondaryStat;
	private final ImmutableList<Supplier<Ability>> abilities;
	private final MAbility manaAbility;
	
	Skill(Stat primaryStat, Stat secondaryStat, ImmutableList<Supplier<Ability>> abilities, MAbility manaAbility) {
		this.primaryStat = primaryStat;
		this.secondaryStat = secondaryStat;
		this.abilities = abilities;
		this.manaAbility = manaAbility;
	}

	public ImmutableList<Supplier<Ability>> getAbilities() {
		return abilities;
	}
	
	public String getDescription(Locale locale) {
		return Lang.getMessage(SkillMessage.valueOf(this.name() + "_DESC"), locale);
	}
	
	public String getDisplayName(Locale locale) {
		return Lang.getMessage(SkillMessage.valueOf(this.name().toUpperCase() + "_NAME"), locale);
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
