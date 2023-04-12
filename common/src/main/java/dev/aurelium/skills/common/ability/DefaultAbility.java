package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.util.NamespacedId;

public class DefaultAbility implements Ability {

    private final NamespacedId id;

    public DefaultAbility(NamespacedId id) {
        this.id = id;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public Skill getSkill() {
        return null;
    }

}
