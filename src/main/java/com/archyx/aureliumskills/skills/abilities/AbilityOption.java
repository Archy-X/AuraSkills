package com.archyx.aureliumskills.skills.abilities;

public class AbilityOption {

    private boolean enabled;
    private double baseValue;
    private double valuePerLevel;

    public AbilityOption(boolean enabled, double baseValue, double valuePerLevel) {
        this.enabled = enabled;
        this.baseValue = baseValue;
        this.valuePerLevel = valuePerLevel;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public double getValuePerLevel() {
        return valuePerLevel;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
