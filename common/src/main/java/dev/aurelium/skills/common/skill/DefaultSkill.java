package dev.aurelium.skills.common.skill;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.util.NamespacedId;

public class DefaultSkill implements Skill {

    private final NamespacedId id;

    public DefaultSkill(NamespacedId id) {
        this.id = id;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

}
