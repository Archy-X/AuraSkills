package dev.aurelium.skills.api.player;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.api.stat.StatModifier;

public interface SkillsPlayer {

    double getSkillXp(Skill skill);

    void addSkillXp(Skill skill, double amountToAdd);

    void addSkillXpRaw(Skill skill, double amountToAdd);

    int setSkillXp(Skill skill, double amount);

    int getSkillLevel(Skill skill);

    void setSkillLevel(Skill skill, int level);

    double getStatLevel(Stat stat);

    double getBaseStatLevel();

    void setStatLevel(Stat stat, double level);

    double addStatLevel(Stat stat, double level);

    double getMana();

    double getMaxMana();

    void setMana(double mana);

    int getPowerLevel();

    boolean addStatModifier(StatModifier statModifier);

    boolean removeStatModifier(String name);

    int getAbilityLevel(Ability ability);

    int getManaAbilityLevel(ManaAbility manaAbility);

}
