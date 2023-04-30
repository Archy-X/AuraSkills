package dev.aurelium.skills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.skill.SkillProperties;
import dev.aurelium.skills.api.skill.Skills;
import dev.aurelium.skills.common.registry.Registry;

public class SkillRegistry extends Registry<Skill, SkillProperties> {

    public SkillRegistry() {
        super(Skill.class, SkillProperties.class);
    }

    @Override
    public void registerDefaults() {
        for (Skills skill : Skills.values()) { // Register each default skill
            // Construct registered skill
            ImmutableList<Ability> abilities = SkillDefaults.getDefaultAbilities(skill);
            ManaAbility manaAbility = SkillDefaults.getDefaultManaAbility(skill);
            SkillProperties skillProperties = new DefaultSkill(skill, abilities, manaAbility);
            // Register
            register(skill.getId(), skill, skillProperties);
        }
    }
}
