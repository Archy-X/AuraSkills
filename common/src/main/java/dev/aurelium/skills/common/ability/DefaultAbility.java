package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.ability.AbilityProperties;
import dev.aurelium.skills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DefaultAbility implements AbilityProperties {

    private final Ability ability;
    private final Skill skill;
    private final Set<String> optionKeys;

    public DefaultAbility(Ability ability, Skill skill, Set<String> optionKeys) {
        this.ability = ability;
        this.skill = skill;
        this.optionKeys = optionKeys;
    }

    @Override
    public @NotNull Ability getAbility() {
        return ability;
    }

    @Override
    public Skill getSkill() {
        return skill;
    }

    @Override
    public Set<String> getOptionKeys() {
        return optionKeys;
    }
}
