package com.archyx.aureliumskills.mana;

public class ManaAbilityOption {

    private final boolean enabled;
    private final double baseValue;
    private final double valuePerLevel;
    private final double baseCooldown;
    private final double cooldownPerLevel;
    private final int baseManaCost;
    private final int manaCostPerLevel;
    private final int unlock;
    private final int levelUp;
    private final int maxLevel;

    public ManaAbilityOption(boolean enabled, double baseValue, double valuePerLevel, double baseCooldown, double cooldownPerLevel, int baseManaCost, int manaCostPerLevel, int unlock, int levelUp, int maxLevel) {
        this.enabled = enabled;
        this.baseValue = baseValue;
        this.valuePerLevel = valuePerLevel;
        this.baseCooldown = baseCooldown;
        this.cooldownPerLevel = cooldownPerLevel;
        this.baseManaCost = baseManaCost;
        this.manaCostPerLevel = manaCostPerLevel;
        this.unlock = unlock;
        this.levelUp = levelUp;
        this.maxLevel = maxLevel;
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

    public double getBaseCooldown() {
        return baseCooldown;
    }

    public double getCooldownPerLevel() {
        return cooldownPerLevel;
    }

    public int getBaseManaCost() {
        return baseManaCost;
    }

    public int getManaCostPerLevel() {
        return manaCostPerLevel;
    }

    public int getUnlock() {
        return unlock;
    }

    public int getLevelUp() {
        return levelUp;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

}
