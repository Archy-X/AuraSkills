package dev.aurelium.auraskills.api.skill;

public class Multiplier {

    private final String name;
    private final Skill skill;
    private final double value;

    public Multiplier(String name, Skill skill, double value) {
        this.name = name;
        this.skill = skill;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public Skill skill() {
        return skill;
    }

    public double value() {
        return value;
    }

    public boolean isGlobal() {
        return skill == null;
    }

}
