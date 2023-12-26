package dev.aurelium.auraskills.common.modifier;

public record DamageModifier(double value, Operation operation) {

    public static DamageModifier none() {
        return new DamageModifier(0.0, Operation.NONE);
    }

    public enum Operation {

        MULTIPLY,
        ADD_BASE,
        ADD_COMBINED,
        NONE

    }

}
