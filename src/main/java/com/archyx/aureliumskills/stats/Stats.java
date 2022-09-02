package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.StatMessage;

import java.util.Locale;

import org.jetbrains.annotations.Nullable;

public enum Stats implements Stat {

	STRENGTH,
	HEALTH,
	REGENERATION,
	LUCK,
	WISDOM,
	TOUGHNESS;

	@Override
	public @Nullable String getDisplayName(@Nullable Locale locale) {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_NAME"), locale);
	}

	@Override
	public @Nullable String getColor(@Nullable Locale locale) {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_COLOR"), locale);
	}

	@Override
	public @Nullable String getSymbol(@Nullable Locale locale) {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_SYMBOL"), locale);
	}

	@Override
	public @Nullable String getDescription(@Nullable Locale locale) {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_DESC"), locale);
	}

}
