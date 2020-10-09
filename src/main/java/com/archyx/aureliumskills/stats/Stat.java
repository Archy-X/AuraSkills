package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.StatMessage;

public enum Stat {

	HEALTH,
	STRENGTH,
	REGENERATION,
	LUCK,
	WISDOM,
	TOUGHNESS;

	public String getDisplayName() {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_NAME"));
	}
	
	public String getColor() {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_COLOR"));
	}
	
	public String getSymbol() {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_SYMBOL"));
	}

	public String getDescription() {
		return Lang.getMessage(StatMessage.valueOf(this.name() + "_DESC"));
	}

}
