package dev.aurelium.auraskills.api.util;

public abstract class AuraSkillsModifier<T> {

    protected final String name;
    protected final T type;
    protected final double value;

    public AuraSkillsModifier(String name, T type, double value) {
        this.name = name;
        this.type = type;
        this.value = value;
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

    @Override
    public String toString() {
        return name + "," + type + "," + value;
    }
}
