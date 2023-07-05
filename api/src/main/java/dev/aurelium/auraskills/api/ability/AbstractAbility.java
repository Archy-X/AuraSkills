package dev.aurelium.auraskills.api.ability;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.registry.NamespacedId;

public interface AbstractAbility {

    Skill getSkill();

    NamespacedId getId();

}
