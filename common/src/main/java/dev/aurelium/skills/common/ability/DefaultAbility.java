package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.ability.AbilityProperties;
import dev.aurelium.skills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

public class DefaultAbility implements AbilityProperties {

    private final Ability ability;
    private final Skill skill;

    public DefaultAbility(Ability ability, Skill skill) {
        this.ability = ability;
        this.skill = skill;
    }

    @Override
    public @NotNull Ability getAbility() {
        return ability;
    }

    @Override
    public Skill getSkill() {
        return skill;
    }
}
