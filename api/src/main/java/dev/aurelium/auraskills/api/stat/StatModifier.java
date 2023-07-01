package dev.aurelium.auraskills.api.stat;

public class StatModifier {

    private final String name;
    private final Stat stat;
    private final double value;

    public StatModifier(String name, Stat stat, double value) {
        this.name = name;
        this.stat = stat;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public Stat stat() {
        return stat;
    }

    public double value() {
        return value;
    }


}
