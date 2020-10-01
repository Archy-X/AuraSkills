package com.archyx.aureliumskills.skills.abilities.mana_abilities;

public class ManaAbilityOption {

    private final boolean enabled;
    private final double baseValue;
    private final double valuePerLevel;
    private final int baseCooldown;
    private final int cooldownPerLevel;
    private final int baseManaCost;
    private final int manaCostPerLevel;

    public ManaAbilityOption(boolean enabled, double baseValue, double valuePerLevel, int baseCooldown, int cooldownPerLevel, int baseManaCost, int manaCostPerLevel) {
        this.enabled = enabled;
        this.baseValue = baseValue;
        this.valuePerLevel = valuePerLevel;
        this.baseCooldown = baseCooldown;
        this.cooldownPerLevel = cooldownPerLevel;
        this.baseManaCost = baseManaCost;
        this.manaCostPerLevel = manaCostPerLevel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public double getValuePerLevel() {
        return valuePerLevel;
    }

    public int getBaseCooldown() {
        return baseCooldown;
    }

    public int getCooldownPerLevel() {
        return cooldownPerLevel;
    }

    public int getBaseManaCost() {
        return baseManaCost;
    }

    public int getManaCostPerLevel() {
        return manaCostPerLevel;
    }

}
