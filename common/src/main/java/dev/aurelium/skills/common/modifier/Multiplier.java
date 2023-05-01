package dev.aurelium.skills.common.modifier;

import dev.aurelium.skills.api.skill.Skill;

public record Multiplier(String name, Skill skill, double value) {

    public boolean isGlobal() {
        return skill == null;
    }

}
