package dev.auramc.auraskills.common.api.implementation;


import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.player.SkillsPlayer;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.stat.Stat;
import dev.auramc.auraskills.api.stat.StatModifier;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;

import java.util.Locale;
import java.util.UUID;

public class ApiSkillsPlayer implements SkillsPlayer {

    private final PlayerData playerData;
    private final AuraSkillsPlugin plugin;

    public ApiSkillsPlayer(PlayerData playerData) {
        this.playerData = playerData;
        this.plugin = playerData.getPlugin();
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    @Override
    public UUID getUuid() {
        return playerData.getUuid();
    }

    @Override
    public double getSkillXp(Skill skill) {
        return playerData.getSkillXp(skill);
    }

    @Override
    public void addSkillXp(Skill skill, double amountToAdd) {
        plugin.getLeveler().addXp(playerData, skill, amountToAdd);
    }

    @Override
    public void addSkillXpRaw(Skill skill, double amountToAdd) {
        playerData.addSkillXp(skill, amountToAdd);
        plugin.getLeveler().checkLevelUp(playerData, skill);
    }

    @Override
    public void setSkillXp(Skill skill, double amount) {
        playerData.setSkillXp(skill, amount);
        plugin.getLeveler().checkLevelUp(playerData, skill);
    }

    @Override
    public int getSkillLevel(Skill skill) {
        return playerData.getSkillLevel(skill);
    }

    @Override
    public void setSkillLevel(Skill skill, int level) {
        playerData.setSkillLevel(skill, level);
    }

    @Override
    public double getStatLevel(Stat stat) {
        return playerData.getStatLevel(stat);
    }

    @Override
    public double getBaseStatLevel(Stat stat) {
        return playerData.getBaseStatLevel(stat);
    }

    @Override
    public double getMana() {
        return playerData.getMana();
    }

    @Override
    public double getMaxMana() {
        return playerData.getMaxMana();
    }

    @Override
    public void setMana(double mana) {
        playerData.setMana(mana);
    }

    @Override
    public int getPowerLevel() {
        return playerData.getPowerLevel();
    }

    @Override
    public void addStatModifier(StatModifier statModifier) {
        playerData.addStatModifier(statModifier);
    }

    @Override
    public void removeStatModifier(String name) {
        playerData.removeStatModifier(name);
    }

    @Override
    public int getAbilityLevel(Ability ability) {
        return playerData.getAbilityLevel(ability);
    }

    @Override
    public int getManaAbilityLevel(ManaAbility manaAbility) {
        return playerData.getManaAbilityLevel(manaAbility);
    }

    @Override
    public Locale getLocale() {
        return playerData.getLocale();
    }

}
