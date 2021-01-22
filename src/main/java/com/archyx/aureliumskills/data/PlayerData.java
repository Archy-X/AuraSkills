package com.archyx.aureliumskills.data;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Health;
import com.archyx.aureliumskills.stats.Luck;
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

    public void addStatModifier(StatModifier modifier) {
        addStatModifier(modifier, true);
    }

    public void addStatModifier(StatModifier modifier, boolean reload) {
        // Removes if already existing
        if (statModifiers.containsKey(modifier.getName())) {
            StatModifier oldModifier = statModifiers.get(modifier.getName());
            if (oldModifier.getStat() == modifier.getStat() && oldModifier.getValue() == modifier.getValue()) {
                return;
            }
            removeStatModifier(modifier.getName());
        }
        statModifiers.put(modifier.getName(), modifier);
        setStatLevel(modifier.getStat(), getStatLevel(modifier.getStat()) + modifier.getValue());
        // Reloads stats
        if (reload) {
            if (modifier.getStat() == Stat.HEALTH) {
                new Health(plugin).reload(player);
            } else if (modifier.getStat() == Stat.LUCK) {
                new Luck(plugin).reload(player);
            }
        }
    }

    public void removeStatModifier(String name) {
        removeStatModifier(name, true);
    }

    public void removeStatModifier(String name, boolean reload) {
        StatModifier modifier = statModifiers.get(name);
        if (modifier == null) return;
        setStatLevel(modifier.getStat(), statLevels.get(modifier.getStat()) - modifier.getValue());
        statModifiers.remove(name);
        // Reloads stats
        if (reload) {
            if (modifier.getStat() == Stat.HEALTH) {
                new Health(plugin).reload(player);
            } else if (modifier.getStat() == Stat.LUCK) {
                new Luck(plugin).reload(player);
            }
        }
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
