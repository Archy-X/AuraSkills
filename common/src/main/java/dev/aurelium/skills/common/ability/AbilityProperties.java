package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface AbilityProperties {

    @NotNull
    Ability ability();

    Skill skill();

    Set<String> optionKeys();

}
