package com.archyx.aureliumskills.modifier;

import java.util.Objects;

public class MultiplierModifier {

    private final String name;
    private final double value; // The value represents the percent more XP gained

    public MultiplierModifier(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiplierModifier that = (MultiplierModifier) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
