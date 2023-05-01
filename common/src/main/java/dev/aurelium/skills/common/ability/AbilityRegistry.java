package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.ability.AbilityProperties;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.skill.Skills;
import dev.aurelium.skills.common.registry.Registry;
import dev.aurelium.skills.common.skill.SkillDefaults;

public class AbilityRegistry extends Registry<Ability, AbilityProperties> {

    public AbilityRegistry() {
        super(Ability.class, AbilityProperties.class);
    }

    @Override
    public void registerDefaults() {
        for (Skills skill : Skills.values()) { // Register default abilities for each default skill
            for (Ability ability : SkillDefaults.getDefaultAbilities(skill)) {
                AbilityProperties properties = new DefaultAbility(ability, skill, SkillDefaults.getOptionKeys(ability));
                register(ability.getId(), ability, properties);
            }
        }
    }

    /**
     * Gets the skill of an ability
     *
     * @param ability The ability
     * @return The skill the ability belongs to
     */
    public Skill getSkill(Ability ability) {
        AbilityProperties prop = getProperties(ability);
        if (prop != null) {
            return prop.skill();
        } else {
            throw new IllegalArgumentException("Ability " + ability.getId() + " is not registered!");
        }
    }

}
