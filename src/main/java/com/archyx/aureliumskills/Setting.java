package com.archyx.aureliumskills;

public enum Setting {

	HEALTH_MODIFIER("health.modifier", 0.5),
	MAX_HEALTH("health.max-health", 1000),
	HEALTH_SCALING("health.health-scaling", true),
	HP_INDICATOR_SCALING("health.hp-indicator-scaling", 5),
	STRENGTH_MODIFIER("strength.modifier", 0.1),
	TOUGHNESS_MODIFIER("toughness.new-modifier", 1),
	CUSTOM_REGEN_MECHANICS("regeneration.custom-regen-mechanics", false),
	BASE_REGEN("regeneration.base-regen", 1),
	SATURATED_MODIFIER("regeneration.saturated-modifier", 0.05),
	HUNGER_FULL_MODIFIER("regeneration.hunger-full-modifier", 0.025),
	HUNGER_ALMOST_FULL_MODIFIER("regeneration.hunger-almost-full-modifier", 0.025),
	SATURATED_DELAY("regeneration.saturated-delay", 20),
	HUNGER_DELAY("regeneration.hunger-delay", 60),
	LUCK_MODIFIER("luck.modifier", 0.1),
	DOUBLE_DROP_MODIFIER("luck.double-drop-modifier", 0.005),
	DOUBLE_DROP_PERCENT_MAX("luck.double-drop-percent-max", 100),
	EXPERIENCE_MODIFIER("wisdom.experience-modifier", 0.01),
	ENABLE_SKILL_POINTS("enable-skill-points", true),
	ANVIL_COST_MODIFIER("wisdom.anvil-cost-modifier", 0.25),
	STRENGTH_HAND_DAMAGE("strength.hand-damage", true),
	STRENGTH_BOW_DAMAGE("strength.bow-damage", true),
	RESET_ARMOR_ATTRIBUTE("toughness.reset-armor-attribute", true),
	DISPLAY_DAMAGE_WITH_HEALTH_SCALING("strength.display-damage-with-health-scaling", true),
	DEFENSE_MAX("defense.max", 100.0),
	DEFENSE_MIN("defense.min", 0),
	DAMAGE_HOLOGRAMS("damage-holograms", true);

	private String path;
	private double defDouble;
	private boolean defBoolean;
	private String type;
	
	private Setting(String path, double def) {
		this.path = path;
		this.defDouble = def;
		this.type = "double";
	}
	
	private Setting(String path, boolean def) {
		this.path = path;
		this.defBoolean = def;
		this.type = "boolean";
	}
	
	public String getType() {
		return type;
	}
	
	public String getPath() {
		return path;
	}
	
	public double getDefaultDouble() {
		return defDouble;
	}
	
	public boolean getDefaultBoolean() {
		return defBoolean;
	}
}
