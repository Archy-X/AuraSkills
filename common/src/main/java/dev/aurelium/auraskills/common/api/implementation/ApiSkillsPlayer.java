package dev.aurelium.auraskills.common.api.implementation;


import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.player.SkillsPlayer;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;

import java.util.Locale;
import java.util.UUID;

public class ApiSkillsPlayer implements SkillsPlayer {

    private final User user;
    private final AuraSkillsPlugin plugin;

    public ApiSkillsPlayer(User user) {
        this.user = user;
        this.plugin = user.getPlugin();
    }

    public User getUser() {
        return user;
    }

    @Override
    public UUID getUuid() {
        return user.getUuid();
    }

    @Override
    public double getSkillXp(Skill skill) {
        return user.getSkillXp(skill);
    }

    @Override
    public void addSkillXp(Skill skill, double amountToAdd) {
        plugin.getLevelManager().addXp(user, skill, amountToAdd);
    }

    @Override
    public void addSkillXpRaw(Skill skill, double amountToAdd) {
        user.addSkillXp(skill, amountToAdd);
        plugin.getLevelManager().checkLevelUp(user, skill);
    }

    @Override
    public void setSkillXp(Skill skill, double amount) {
        user.setSkillXp(skill, amount);
        plugin.getLevelManager().checkLevelUp(user, skill);
    }

    @Override
    public int getSkillLevel(Skill skill) {
        return user.getSkillLevel(skill);
    }

    @Override
    public void setSkillLevel(Skill skill, int level) {
        user.setSkillLevel(skill, level);
    }

    @Override
    public double getStatLevel(Stat stat) {
        return user.getStatLevel(stat);
    }

    @Override
    public double getBaseStatLevel(Stat stat) {
        return user.getBaseStatLevel(stat);
    }

    @Override
    public double getMana() {
        return user.getMana();
    }

    @Override
    public double getMaxMana() {
        return user.getMaxMana();
    }

    @Override
    public void setMana(double mana) {
        user.setMana(mana);
    }

    @Override
    public int getPowerLevel() {
        return user.getPowerLevel();
    }

    @Override
    public void addStatModifier(StatModifier statModifier) {
        user.addStatModifier(statModifier);
    }

    @Override
    public void removeStatModifier(String name) {
        user.removeStatModifier(name);
    }

    @Override
    public int getAbilityLevel(Ability ability) {
        return user.getAbilityLevel(ability);
    }

    @Override
    public int getManaAbilityLevel(ManaAbility manaAbility) {
        return user.getManaAbilityLevel(manaAbility);
    }

    @Override
    public Locale getLocale() {
        return user.getLocale();
    }

}
