package dev.aurelium.skills.api.skill;

import dev.aurelium.skills.api.util.NamespacedId;

public interface Skill {

    NamespacedId getId();

    default boolean equals(Skill skill) {
        return getId().equals(skill.getId());
    }

}
