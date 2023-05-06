package dev.auramc.auraskills.common.mana;

import dev.auramc.auraskills.api.mana.ManaAbilities;
import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.mana.ManaAbilityProvider;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.skill.Skills;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.registry.Registry;
import dev.auramc.auraskills.common.skill.SkillDefaults;

import java.util.Locale;

/**
 * Registry for storing mana abilities and their properties.
 */
public class ManaAbilityRegistry extends Registry<ManaAbility, ManaAbilityProperties> implements ManaAbilityProvider {

    private final AuraSkillsPlugin plugin;

    public ManaAbilityRegistry(AuraSkillsPlugin plugin) {
        super(ManaAbility.class, ManaAbilityProperties.class);
        this.plugin = plugin;
    }

    @Override
    public void registerDefaults() {
        for (Skills skills : Skills.values()) {
            ManaAbilities manaAbility = SkillDefaults.getDefaultManaAbility(skills);
            if (manaAbility != null) { // Register if mana ability exists for skill
                ManaAbilityProperties properties = new DefaultManaAbility(manaAbility, skills, SkillDefaults.getOptionKeys(manaAbility));
                register(manaAbility.getId(), manaAbility, properties);
                // Inject ManaAbilityProvider into each default mana ability
                injectSelf(manaAbility, ManaAbilityProvider.class);
            }
        }
    }

    @Override
    public Skill getSkill(ManaAbility manaAbility) {
        return getProperties(manaAbility).skill();
    }

    @Override
    public String getDisplayName(ManaAbility manaAbility, Locale locale) {
        return plugin.getMessageProvider().getManaAbilityDisplayName(manaAbility, locale);
    }

    @Override
    public String getDescription(ManaAbility manaAbility, Locale locale) {
        return plugin.getMessageProvider().getManaAbilityDescription(manaAbility, locale);
    }

    @Override
    public String getInfo(ManaAbility manaAbility, Locale locale) {
        return plugin.getMessageProvider().getManaAbilityInfo(manaAbility, locale);
    }

    @Override
    public double getBaseValue(ManaAbility manaAbility) {
        return plugin.getManaAbilityManager().getBaseValue(manaAbility);
    }

    @Override
    public double getValuePerLevel(ManaAbility manaAbility) {
        return plugin.getManaAbilityManager().getValuePerLevel(manaAbility);
    }

    @Override
    public double getBaseCooldown(ManaAbility manaAbility) {
        return plugin.getManaAbilityManager().getBaseCooldown(manaAbility);
    }

    @Override
    public double getCooldownPerLevel(ManaAbility manaAbility) {
        return plugin.getManaAbilityManager().getCooldownPerLevel(manaAbility);
    }

    @Override
    public double getBaseManaCost(ManaAbility manaAbility) {
        return plugin.getManaAbilityManager().getBaseManaCost(manaAbility);
    }

    @Override
    public double getManaCostPerLevel(ManaAbility manaAbility) {
        return plugin.getManaAbilityManager().getManaCostPerLevel(manaAbility);
    }

    @Override
    public int getUnlock(ManaAbility manaAbility) {
        return plugin.getManaAbilityManager().getUnlock(manaAbility);
    }

    @Override
    public int getLevelUp(ManaAbility manaAbility) {
        return plugin.getManaAbilityManager().getLevelUp(manaAbility);
    }

    @Override
    public int getMaxLevel(ManaAbility manaAbility) {
        return plugin.getManaAbilityManager().getMaxLevel(manaAbility);
    }
}
