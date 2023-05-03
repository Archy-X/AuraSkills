package dev.aurelium.skills.common.message;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;

import java.util.Locale;

public interface MessageProvider {

    String get(MessageKey key, Locale locale);

    String getSkillDisplayName(Skill skill, Locale locale);

    String getSkillDescription(Skill skill, Locale locale);

    String getStatDisplayName(Stat stat, Locale locale);

    String getStatDescription(Stat stat, Locale locale);

    String getStatColor(Stat stat, Locale locale);

    String getStatSymbol(Stat stat, Locale locale);

    String getAbilityDisplayName(Ability ability, Locale locale);

    String getAbilityDescription(Ability ability, Locale locale);

    String getAbilityInfo(Ability ability, Locale locale);

    String getManaAbilityDisplayName(ManaAbility ability, Locale locale);

    String getManaAbilityDescription(ManaAbility ability, Locale locale);

    String getManaAbilityInfo(ManaAbility ability, Locale locale);

    Locale getDefaultLanguage();

}
