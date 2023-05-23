package dev.auramc.auraskills.api.ability;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.registry.NamespacedId;

public interface AbstractAbility {

    Skill getSkill();

    NamespacedId getId();
}
