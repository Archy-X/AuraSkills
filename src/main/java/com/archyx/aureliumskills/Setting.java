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
	SATURATED_MODIFIER("regeneration.saturated-modifier", 0.007),
	HUNGER_FULL_MODIFIER("regeneration.hunger-full-modifier", 0.006),
	HUNGER_ALMOST_FULL_MODIFIER("regeneration.hunger-almost-full-modifier", 0.005),
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
	DAMAGE_HOLOGRAMS("damage-holograms", true),
	DAMAGE_HOLOGRAMS_SCALING("damage-holograms-scaling", true),
	DAMAGE_HOLOGRAMS_OFFSET_X("damage-holograms-offset.x", 0.0),
	DAMAGE_HOLOGRAMS_OFFSET_Y("damage-holograms-offset.y", 0.0),
	DAMAGE_HOLOGRAMS_OFFSET_Z("damage-holograms-offset.z", 0.0),
	DAMAGE_HOLOGRAMS_RANDOM_ENABLED("damage-holograms-offset.random.enabled", false),
	DAMAGE_HOLOGRAMS_RANDOM_X_MIN("damage-holograms-offset.random.x-min", -0.5),
	DAMAGE_HOLOGRAMS_RANDOM_X_MAX("damage-holograms-offset.random.x-max", 0.5),
	DAMAGE_HOLOGRAMS_RANDOM_Y_MIN("damage-holograms-offset.random.y-min", 0),
	DAMAGE_HOLOGRAMS_RANDOM_Y_MAX("damage-holograms-offset.random.y-max", 0),
	DAMAGE_HOLOGRAMS_RANDOM_Z_MIN("damage-holograms-offset.random.z-min", -0.5),
	DAMAGE_HOLOGRAMS_RANDOM_Z_MAX("damage-holograms-offset.random.z-max", 0.5);

	private final String path;
	private double defDouble;
	private boolean defBoolean;
	private final String type;
	
	Setting(String path, double def) {
		this.path = path;
		this.defDouble = def;
		this.type = "double";
	}
	
	Setting(String path, boolean def) {
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
