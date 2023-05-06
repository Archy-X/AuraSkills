package dev.auramc.auraskills.common.ability;

import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface AbilityProperties {

    @NotNull
    Ability ability();

    Skill skill();

    Set<String> optionKeys();

}
