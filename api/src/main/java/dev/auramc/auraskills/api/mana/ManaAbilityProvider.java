package dev.auramc.auraskills.api.mana;

import dev.auramc.auraskills.api.skill.Skill;

import java.util.Locale;

public interface ManaAbilityProvider {

    Skill getSkill(ManaAbility manaAbility);

    String getDisplayName(ManaAbility manaAbility, Locale locale);

    String getDescription(ManaAbility manaAbility, Locale locale);

    String getInfo(ManaAbility manaAbility, Locale locale);

    double getBaseValue(ManaAbility manaAbility);

    double getValuePerLevel(ManaAbility manaAbility);

    double getBaseCooldown(ManaAbility manaAbility);

    double getCooldownPerLevel(ManaAbility manaAbility);

    double getBaseManaCost(ManaAbility manaAbility);

    double getManaCostPerLevel(ManaAbility manaAbility);

    int getUnlock(ManaAbility manaAbility);

    int getLevelUp(ManaAbility manaAbility);

    int getMaxLevel(ManaAbility manaAbility);

}
