package dev.aurelium.skills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.skill.SkillProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultSkill implements SkillProperties {

    private final Skill skill;
    private final ImmutableList<Ability> abilities;
    private final ManaAbility manaAbility;

    public DefaultSkill(Skill skill, ImmutableList<Ability> abilities, ManaAbility manaAbility) {
        this.skill = skill;
        this.abilities = abilities;
        this.manaAbility = manaAbility;
    }

    @NotNull
    public Skill getSkill() {
        return skill;
    }

    @NotNull
    public ImmutableList<Ability> getAbilities() {
        return abilities;
    }

    @Nullable
    public ManaAbility getManaAbility() {
        return manaAbility;
    }

}
