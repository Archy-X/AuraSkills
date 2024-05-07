package dev.aurelium.auraskills.api.mana;

import dev.aurelium.auraskills.api.option.OptionedProvider;
import dev.aurelium.auraskills.api.skill.Skill;

import java.util.Locale;

public interface ManaAbilityProvider extends OptionedProvider<ManaAbility> {

    Skill getSkill(ManaAbility manaAbility);

    String getDisplayName(ManaAbility manaAbility, Locale locale, boolean formatted);

    String getDescription(ManaAbility manaAbility, Locale locale, boolean formatted);

    boolean isEnabled(ManaAbility manaAbility);

    double getBaseValue(ManaAbility manaAbility);

    double getValuePerLevel(ManaAbility manaAbility);

    double getValue(ManaAbility manaAbility, int level);

    double getDisplayValue(ManaAbility manaAbility, int level);

    double getBaseCooldown(ManaAbility manaAbility);

    double getCooldownPerLevel(ManaAbility manaAbility);

    double getCooldown(ManaAbility manaAbility, int level);

    double getBaseManaCost(ManaAbility manaAbility);

    double getManaCostPerLevel(ManaAbility manaAbility);

    double getManaCost(ManaAbility manaAbility, int level);

    int getUnlock(ManaAbility manaAbility);

    int getLevelUp(ManaAbility manaAbility);

    int getMaxLevel(ManaAbility manaAbility);

}
