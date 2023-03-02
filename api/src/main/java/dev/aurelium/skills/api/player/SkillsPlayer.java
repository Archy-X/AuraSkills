package dev.aurelium.skills.api.player;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;

public interface SkillsPlayer {

    double getSkillXp(Skill skill);

    void addSkillXp(Skill skill, double amountToAdd);

    int setSkillXp(Skill skill, double amount);

    int getSkillLevel(Skill skill);

    void setSkillLevel(Skill skill, int level);

    double getStatLevel(Stat stat);

    void setStatLevel(Stat stat, double level);

    double addStatLevel(Stat stat, double level);

    double getMana();

    double getMaxMana();



}
