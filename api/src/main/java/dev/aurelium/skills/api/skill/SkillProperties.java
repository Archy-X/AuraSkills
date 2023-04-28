package dev.aurelium.skills.api.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.ability.ManaAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SkillProperties {

    @NotNull
    Skill getSkill();

    @NotNull
    ImmutableList<Ability> getAbilities();

    @Nullable
    default ManaAbility getManaAbility() {
        return null;
    }

}
