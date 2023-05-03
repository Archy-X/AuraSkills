package dev.aurelium.skills.api.ability;

import dev.aurelium.skills.api.skill.Skill;

import java.util.Locale;

public interface AbilityProvider {

    Skill getSkill(Ability ability);

    String getDisplayName(Ability ability, Locale locale);

    String getDescription(Ability ability, Locale locale);

    String getInfo(Ability ability, Locale locale);

    double getBaseValue(Ability ability);

    double getSecondaryBaseValue(Ability ability);

    double getValuePerLevel(Ability ability);

    double getSecondaryValuePerLevel(Ability ability);

    int getUnlock(Ability ability);

    int getLevelUp(Ability ability);

    int getMaxLevel(Ability ability);

}
