package dev.aurelium.skills.common.mana;

import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.mana.ManaAbilityProperties;
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
