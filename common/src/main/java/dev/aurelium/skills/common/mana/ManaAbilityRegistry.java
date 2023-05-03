package dev.aurelium.skills.common.mana;

import dev.aurelium.skills.api.mana.ManaAbilities;
import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.mana.ManaAbilityProvider;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.skill.Skills;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.registry.Registry;
import dev.aurelium.skills.common.skill.SkillDefaults;

import java.util.Locale;

public class ManaAbilityRegistry extends Registry<ManaAbility, ManaAbilityProperties> implements ManaAbilityProvider {

    private final AureliumSkillsPlugin plugin;

    public ManaAbilityRegistry(AureliumSkillsPlugin plugin) {
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
