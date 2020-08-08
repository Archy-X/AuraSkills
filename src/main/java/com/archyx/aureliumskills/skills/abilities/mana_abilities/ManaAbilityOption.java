package com.archyx.aureliumskills.skills.abilities.mana_abilities;

public class ManaAbilityOption {

    private double baseValue;
    private double valuePerLevel;
    private int baseCooldown;
    private int cooldownPerLevel;
    private int baseManaCost;
    private int manaCostPerLevel;

    public ManaAbilityOption(double baseValue, double valuePerLevel, int baseCooldown, int cooldownPerLevel, int baseManaCost, int manaCostPerLevel) {
        this.baseValue = baseValue;
        this.valuePerLevel = valuePerLevel;
        this.baseCooldown = baseCooldown;
        this.cooldownPerLevel = cooldownPerLevel;
        this.baseManaCost = baseManaCost;
        this.manaCostPerLevel = manaCostPerLevel;
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
