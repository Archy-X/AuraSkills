package com.archyx.aureliumskills.data;

import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;

import java.util.Map;
import java.util.UUID;

public class PlayerDataState {

    private final UUID uuid;
    private final Map<Skill, Integer> skillLevels;
    private final Map<Skill, Double> skillXp;
    private final Map<String, StatModifier> statModifiers;
    private final double mana;

    public PlayerDataState(UUID uuid, Map<Skill, Integer> skillLevels, Map<Skill, Double> skillXp, Map<String, StatModifier> statModifiers, double mana) {
        this.uuid = uuid;
        this.skillLevels = skillLevels;
        this.skillXp = skillXp;
        this.statModifiers = statModifiers;
        this.mana = mana;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<Skill, Integer> getSkillLevels() {
        return skillLevels;
    }

    public Map<Skill, Double> getSkillXp() {
        return skillXp;
    }

    public Map<String, StatModifier> getStatModifiers() {
        return statModifiers;
    }

    public double getMana() {
        return mana;
    }
}
