package dev.aurelium.skills.common.modifier;

import dev.aurelium.skills.api.skill.Skill;

import java.util.Objects;

public record Multiplier(String name, Skill skill, double value) {

    public boolean isGlobal() {
        return skill == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Multiplier that = (Multiplier) o;
        return Double.compare(that.value, value) == 0 && Objects.equals(name, that.name) && Objects.equals(skill, that.skill);
    }

}
