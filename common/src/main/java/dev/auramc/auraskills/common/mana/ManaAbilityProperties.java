package dev.auramc.auraskills.common.mana;

import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ManaAbilityProperties {

    @NotNull
    ManaAbility manaAbility();

    Skill skill();

    Set<String> optionKeys();

}
