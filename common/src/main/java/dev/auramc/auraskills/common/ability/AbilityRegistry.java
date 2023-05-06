package dev.auramc.auraskills.common.ability;

import dev.auramc.auraskills.api.ability.Abilities;
import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.ability.AbilityProvider;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.skill.Skills;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.registry.Registry;
import dev.auramc.auraskills.common.skill.SkillDefaults;

import java.util.Locale;

/**
 * Registry for storing abilities and their properties.
 */
public class AbilityRegistry extends Registry<Ability, AbilityProperties> implements AbilityProvider {

    private final AuraSkillsPlugin plugin;

    public AbilityRegistry(AuraSkillsPlugin plugin) {
        super(Ability.class, AbilityProperties.class);
        this.plugin = plugin;
    }

    @Override
    public void registerDefaults() {
        for (Skills skill : Skills.values()) { // Register default abilities for each default skill
            for (Abilities ability : SkillDefaults.getDefaultAbilities(skill)) {
                AbilityProperties properties = new DefaultAbility(ability, skill, SkillDefaults.getOptionKeys(ability));
                register(ability.getId(), ability, properties);
                // Inject AbilityProvider into each default ability
                injectSelf(ability, AbilityProvider.class);
            }
        }
    }

    @Override
    public Skill getSkill(Ability ability) {
        return getProperties(ability).skill();
    }

    @Override
    public String getDisplayName(Ability ability, Locale locale) {
        return plugin.getMessageProvider().getAbilityDisplayName(ability, locale);
    }

    @Override
    public String getDescription(Ability ability, Locale locale) {
        return plugin.getMessageProvider().getAbilityDescription(ability, locale);
    }

    @Override
    public String getInfo(Ability ability, Locale locale) {
        return plugin.getMessageProvider().getAbilityInfo(ability, locale);
    }

    @Override
    public double getBaseValue(Ability ability) {
        return plugin.getAbilityManager().getBaseValue(ability);
    }

    @Override
    public double getSecondaryBaseValue(Ability ability) {
        return plugin.getAbilityManager().getSecondaryBaseValue(ability);
    }

    @Override
    public double getValuePerLevel(Ability ability) {
        return plugin.getAbilityManager().getValuePerLevel(ability);
    }

    @Override
    public double getSecondaryValuePerLevel(Ability ability) {
        return plugin.getAbilityManager().getSecondaryValuePerLevel(ability);
    }

    @Override
    public int getUnlock(Ability ability) {
        return plugin.getAbilityManager().getUnlock(ability);
    }

    @Override
    public int getLevelUp(Ability ability) {
        return plugin.getAbilityManager().getLevelUp(ability);
    }

    @Override
    public int getMaxLevel(Ability ability) {
        return plugin.getAbilityManager().getMaxLevel(ability);
    }
}
