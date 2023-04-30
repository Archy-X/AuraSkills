package dev.aurelium.skills.common.mana;

import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.mana.ManaAbilityProperties;
import dev.aurelium.skills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DefaultManaAbility implements ManaAbilityProperties {

    private final ManaAbility manaAbility;
    private final Skill skill;
    private final Set<String> optionKeys;

    public DefaultManaAbility(ManaAbility manaAbility, Skill skill, Set<String> optionKeys) {
        this.manaAbility = manaAbility;
        this.skill = skill;
        this.optionKeys = optionKeys;
    }

    @Override
    public @NotNull ManaAbility getManaAbility() {
        return manaAbility;
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
