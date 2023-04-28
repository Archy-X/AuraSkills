package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.ability.ManaAbilityProperties;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.skill.Skills;
import dev.aurelium.skills.common.registry.Registry;
import dev.aurelium.skills.common.skill.SkillDefaults;

public class ManaAbilityRegistry extends Registry<ManaAbility, ManaAbilityProperties> {

    public ManaAbilityRegistry() {
        super(ManaAbility.class, ManaAbilityProperties.class);
    }

    @Override
    public void registerDefaults() {
        for (Skills skills : Skills.values()) {
            ManaAbility manaAbility = SkillDefaults.getDefaultManaAbility(skills);
            if (manaAbility != null) { // Register if mana ability exists for skill
                ManaAbilityProperties properties = new DefaultManaAbility(manaAbility, skills);
                register(manaAbility.getId(), manaAbility, properties);
            }
        }
    }

    /**
     * Gets the skill of a mana ability
     *
     * @param manaAbility The mana ability
     * @return The skill the mana ability belongs to
     */
    public Skill getSkill(ManaAbility manaAbility) {
        ManaAbilityProperties prop = getProperties(manaAbility);
        if (prop != null) {
            return prop.getSkill();
        } else {
            throw new IllegalArgumentException("Mana ability " + manaAbility.getId() + " is not registered!");
        }
    }
}
