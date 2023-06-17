package dev.aurelium.auraskills.common.modifier;

import dev.aurelium.auraskills.api.skill.Skill;

public record Multiplier(String name, Skill skill, double value) {

    public boolean isGlobal() {
        return skill == null;
    }

}
