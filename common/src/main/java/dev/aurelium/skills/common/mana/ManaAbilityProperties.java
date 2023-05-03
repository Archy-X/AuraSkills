package dev.aurelium.skills.common.mana;

import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ManaAbilityProperties {

    @NotNull
    ManaAbility manaAbility();

    Skill skill();

    Set<String> optionKeys();

}
