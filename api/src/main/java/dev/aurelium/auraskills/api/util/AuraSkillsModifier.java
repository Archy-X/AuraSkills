package dev.aurelium.auraskills.api.util;

import org.jetbrains.annotations.NotNull;

public abstract class AuraSkillsModifier<T> {

    protected final String name;
    protected final T type;
    protected final double value;
    protected final Operation operation;

    public AuraSkillsModifier(String name, T type, double value, @NotNull Operation operation) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.operation = operation;
    }

    public String name() {
        return name;
    }

    public T type() {
        return type;
    }

    public double value() {
        return value;
    }

    public Operation operation() {
        return operation;
    }

    @Override
    public String toString() {
        return name + "," + type + "," + value;
    }

    public enum Operation {

        ADD,
        MULTIPLY,
        ADD_PERCENT;

        @NotNull
        public static Operation parse(String name) {
            if (name == null) return Operation.ADD;
            try {
                return Operation.valueOf(name.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                return Operation.ADD;
            }
        }

    }

}
