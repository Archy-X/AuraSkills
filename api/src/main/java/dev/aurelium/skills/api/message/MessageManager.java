package dev.aurelium.skills.api.message;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;

import java.util.Locale;

public interface MessageManager {

    String getMessage(Locale locale, String key);

    String getSkillDisplayName(Locale locale, Skill skill);

    String getSkillDescription(Locale locale, Skill skill);

    String getAbilityDisplayName(Locale locale, Ability ability);

    String getAbilityDescription(Locale locale, Ability ability);

    String getManaAbilityDisplayName(Locale locale, ManaAbility manaAbility);

    String getManaAbilityDescription(Locale locale, ManaAbility manaAbility);

}
