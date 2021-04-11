package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.StatMessage;
import com.archyx.aureliumskills.skills.Skill;

import java.util.Locale;
import java.util.function.Supplier;

public enum Stats implements Stat {

	STRENGTH(new Supplier[] {() -> Skill.FORAGING, () -> Skill.FIGHTING, () -> Skill.SORCERY},
			new Supplier[] {() -> Skill.FARMING, () -> Skill.ARCHERY}),
	HEALTH(new Supplier[] {() -> Skill.FARMING, () -> Skill.ALCHEMY},
			new Supplier[] {() -> Skill.FISHING, () -> Skill.DEFENSE, () -> Skill.HEALING}),
	REGENERATION(new Supplier[] {() -> Skill.EXCAVATION, () -> Skill.ENDURANCE, () -> Skill.HEALING},
			new Supplier[] {() -> Skill.FIGHTING, () -> Skill.AGILITY}),
	LUCK(new Supplier[] {() -> Skill.FISHING, () -> Skill.ARCHERY},
			new Supplier[] {() -> Skill.MINING, () -> Skill.EXCAVATION, () -> Skill.ENCHANTING}),
	WISDOM(new Supplier[] {() -> Skill.AGILITY, () -> Skill.ENCHANTING},
			new Supplier[] {() -> Skill.ALCHEMY, () -> Skill.SORCERY, () -> Skill.FORGING}),
	TOUGHNESS(new Supplier[] {() -> Skill.MINING, () -> Skill.DEFENSE, () -> Skill.FORGING},
			new Supplier[] {() -> Skill.FORAGING, () -> Skill.ENDURANCE});

	private final Supplier<Skill>[] primarySkills;
	private final Supplier<Skill>[] secondarySkills;

	Stats(Supplier<Skill>[] primarySkills, Supplier<Skill>[] secondarySkills) {
		this.primarySkills = primarySkills;
		this.secondarySkills = secondarySkills;
	}

	@Override
	public Supplier<Skill>[] getPrimarySkills() {
		return primarySkills;
	}

	@Override
	public Supplier<Skill>[] getSecondarySkills() {
		return secondarySkills;
	}

	@Override
	public String getDisplayName(Locale locale) {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_NAME"), locale);
	}

	@Override
	public String getColor(Locale locale) {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_COLOR"), locale);
	}

	@Override
	public String getSymbol(Locale locale) {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_SYMBOL"), locale);
	}

	@Override
	public String getDescription(Locale locale) {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_DESC"), locale);
	}

}
