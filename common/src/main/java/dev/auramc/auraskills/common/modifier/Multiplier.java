package dev.auramc.auraskills.common.modifier;

import dev.auramc.auraskills.api.skill.Skill;

public record Multiplier(String name, Skill skill, double value) {

    public boolean isGlobal() {
        return skill == null;
    }

}
