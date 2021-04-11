package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.StatMessage;
import com.archyx.aureliumskills.skills.Skills;

import java.util.Locale;
import java.util.function.Supplier;

public enum Stats implements Stat {

	STRENGTH(new Supplier[] {() -> Skills.FORAGING, () -> Skills.FIGHTING, () -> Skills.SORCERY},
			new Supplier[] {() -> Skills.FARMING, () -> Skills.ARCHERY}),
	HEALTH(new Supplier[] {() -> Skills.FARMING, () -> Skills.ALCHEMY},
			new Supplier[] {() -> Skills.FISHING, () -> Skills.DEFENSE, () -> Skills.HEALING}),
	REGENERATION(new Supplier[] {() -> Skills.EXCAVATION, () -> Skills.ENDURANCE, () -> Skills.HEALING},
			new Supplier[] {() -> Skills.FIGHTING, () -> Skills.AGILITY}),
	LUCK(new Supplier[] {() -> Skills.FISHING, () -> Skills.ARCHERY},
			new Supplier[] {() -> Skills.MINING, () -> Skills.EXCAVATION, () -> Skills.ENCHANTING}),
	WISDOM(new Supplier[] {() -> Skills.AGILITY, () -> Skills.ENCHANTING},
			new Supplier[] {() -> Skills.ALCHEMY, () -> Skills.SORCERY, () -> Skills.FORGING}),
	TOUGHNESS(new Supplier[] {() -> Skills.MINING, () -> Skills.DEFENSE, () -> Skills.FORGING},
			new Supplier[] {() -> Skills.FORAGING, () -> Skills.ENDURANCE});

	private final Supplier<Skills>[] primarySkills;
	private final Supplier<Skills>[] secondarySkills;

	Stats(Supplier<Skills>[] primarySkills, Supplier<Skills>[] secondarySkills) {
		this.primarySkills = primarySkills;
		this.secondarySkills = secondarySkills;
	}

	@Override
	public Supplier<Skills>[] getPrimarySkills() {
		return primarySkills;
	}

	@Override
	public Supplier<Skills>[] getSecondarySkills() {
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
