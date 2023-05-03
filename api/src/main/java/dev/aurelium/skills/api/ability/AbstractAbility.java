package dev.aurelium.skills.api.ability;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.util.NamespacedId;

public interface AbstractAbility {

    Skill getSkill();

    NamespacedId getId();
}
