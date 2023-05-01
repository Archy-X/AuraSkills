package dev.aurelium.skills.api.mana;

import dev.aurelium.skills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ManaAbilityProperties {

    @NotNull
    ManaAbility manaAbility();

    Skill skill();

    Set<String> optionKeys();

}
