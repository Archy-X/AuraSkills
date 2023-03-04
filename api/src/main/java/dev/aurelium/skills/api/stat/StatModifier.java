package dev.aurelium.skills.api.stat;

public class StatModifier {

    private final String name;
    private final Stat stat;
    private final double value;

    public StatModifier(String name, Stat stat, double value) {
        this.name = name;
        this.stat = stat;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Stat getStat() {
        return stat;
    }

    public double getValue() {
        return value;
    }
}
