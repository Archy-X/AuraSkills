package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.StatMessage;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Stats implements Stat {

	STRENGTH,
	HEALTH,
	REGENERATION,
	LUCK,
	WISDOM,
	TOUGHNESS;

	@Override
	public @NotNull String getDisplayName(@Nullable Locale locale) {
	    @Nullable String m = Lang.getMessage(StatMessage.valueOf(this.name() + "_NAME"), locale);
	    assert (null != m);
		return m;
	}

	@Override
	public @NotNull String getColor(@Nullable Locale locale) {
		@Nullable String m = Lang.getMessage(StatMessage.valueOf(this.name() + "_COLOR"), locale);
        assert (null != m);
		return m;
	}

	@Override
	public @NotNull String getSymbol(@Nullable Locale locale) {
		@Nullable String m = Lang.getMessage(StatMessage.valueOf(this.name() + "_SYMBOL"), locale);
        assert (null != m);
		return m;
	}

	@Override
	public @NotNull String getDescription(@Nullable Locale locale) {
		@Nullable String m = Lang.getMessage(StatMessage.valueOf(this.name() + "_DESC"), locale);
        assert (null != m);
		return m;
	}

}
