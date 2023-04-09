package dev.aurelium.skills.common.skill;

import dev.aurelium.skills.api.util.NamespacedId;
import dev.aurelium.skills.common.registry.Namespaced;

public abstract class Skill implements Namespaced {

    private final NamespacedId id;

    public Skill(NamespacedId id) {
        this.id = id;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

}
