package dev.aurelium.skills.api.ability;

import dev.aurelium.skills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface AbilityProperties {

    @NotNull
    Ability ability();

    Skill skill();

    Set<String> optionKeys();

}
