package com.archyx.aureliumskills.skills.abilities;

public class AbilityOption {

    private boolean enabled;
    private double baseValue;
    private double valuePerLevel;
    private boolean hasTwoValues;
    private double baseValue2;
    private double valuePerLevel2;

    public AbilityOption(boolean enabled, double baseValue, double valuePerLevel) {
        this.enabled = enabled;
        this.baseValue = baseValue;
        this.valuePerLevel = valuePerLevel;
    }

    public AbilityOption(boolean enabled, double baseValue1, double valuePerLevel1, double baseValue2, double valuePerLevel2) {
        this.enabled = enabled;
        this.hasTwoValues = true;
        this.baseValue = baseValue1;
        this.valuePerLevel = valuePerLevel1;
        this.baseValue2 = baseValue2;
        this.valuePerLevel2 = valuePerLevel2;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public double getValuePerLevel() {
        return valuePerLevel;
    }

    public boolean hasTwoValues() {
        return hasTwoValues;
    }

    public double getBaseValue2() {
        return baseValue2;
    }

    public double getValuePerLevel2() {
        return valuePerLevel2;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
