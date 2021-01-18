package com.archyx.aureliumskills.data;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PlayerData {

    private final Player player;
    private final AureliumSkills plugin;

    private final Map<Skill, Integer> skillLevels;
    private final Map<Skill, Double> skillXp;

    private final Map<Stat, Double> statLevels;
    private final Map<String, StatModifier> statModifiers;

    private double mana;
    private Locale locale;

    private final Map<Ability, AbilityData> abilityData;

    public PlayerData(Player player, AureliumSkills plugin) {
        this.player = player;
        this.plugin = plugin;
        this.skillLevels = new HashMap<>();
        this.skillXp = new HashMap<>();
        this.statLevels = new HashMap<>();
        this.statModifiers = new HashMap<>();
        this.abilityData = new HashMap<>();
    }

    public Player getPlayer() {
        return player;
    }

    public AureliumSkills getPlugin() {
        return plugin;
    }

    public int getSkillLevel(Skill skill) {
        return skillLevels.getOrDefault(skill, 1);
    }

    public void setSkillLevel(Skill skill, int level) {
        skillLevels.put(skill, level);
    }

    public double getSkillXp(Skill skill) {
        return skillXp.getOrDefault(skill, 0.0);
    }

    public void setSkillXp(Skill skill, double xp) {
        skillXp.put(skill, xp);
    }

    public double getStatLevel(Stat stat) {
        return statLevels.getOrDefault(stat, 0.0);
    }

    public void setStatLevel(Stat stat, double level) {
        statLevels.put(stat, level);
    }

    public StatModifier getStatModifier(String name) {
        return statModifiers.get(name);
    }

    public void addStatModifier(StatModifier statModifier) {
        statModifiers.put(statModifier.getName(), statModifier);
    }

    public void removeStatModifier(String name) {
        statModifiers.remove(name);
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public AbilityData getAbilityData(Ability ability) {
        AbilityData data = abilityData.get(ability);
        if (data == null) {
            data = new AbilityData(ability);
            abilityData.put(ability, data);
        }
        return data;
    }

}
