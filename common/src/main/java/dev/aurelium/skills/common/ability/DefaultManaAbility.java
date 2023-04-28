package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.ability.ManaAbilityProperties;
import dev.aurelium.skills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

public class DefaultManaAbility implements ManaAbilityProperties {

    private final ManaAbility manaAbility;
    private final Skill skill;

    public DefaultManaAbility(ManaAbility manaAbility, Skill skill) {
        this.manaAbility = manaAbility;
        this.skill = skill;
    }

    @Override
    public @NotNull ManaAbility getManaAbility() {
        return manaAbility;
    }

    @Override
    public Skill getSkill() {
        return skill;
    }

}
