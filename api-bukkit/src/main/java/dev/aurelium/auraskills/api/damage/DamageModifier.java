package dev.aurelium.auraskills.api.damage;

public class DamageModifier {

    private final double value;
    private final Operation operation;

    public DamageModifier(double value, Operation operation) {
        this.operation = operation;
        this.value = value;
    }

    public double value() {
        return value;
    }

    public Operation operation() {
        return operation;
    }

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
