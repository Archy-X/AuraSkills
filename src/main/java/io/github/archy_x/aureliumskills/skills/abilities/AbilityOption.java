package io.github.archy_x.aureliumskills.skills.abilities;

public class AbilityOption {

    private double baseValue;
    private double valuePerLevel;

    public AbilityOption(double baseValue, double valuePerLevel) {
        this.baseValue = baseValue;
        this.valuePerLevel = valuePerLevel;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public double getValuePerLevel() {
        return valuePerLevel;
    }
}
