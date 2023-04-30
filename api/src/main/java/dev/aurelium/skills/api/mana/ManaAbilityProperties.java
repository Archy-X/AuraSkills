package dev.aurelium.skills.api.mana;

import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

public interface ManaAbilityProperties {

    @NotNull
    ManaAbility getManaAbility();

    Skill getSkill();

}
