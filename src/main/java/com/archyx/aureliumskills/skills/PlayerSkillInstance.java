package com.archyx.aureliumskills.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSkillInstance {

    private final UUID playerId;
    private final Map<Skill, Integer> levels = new HashMap<>();
    private final Map<Skill, Double> xp = new HashMap<>();

    public PlayerSkillInstance(PlayerSkill playerSkill) {
        this.playerId = playerSkill.getPlayerId();
        for (Skill skill : Skill.values()) {
            levels.put(skill, playerSkill.getSkillLevel(skill));
            xp.put(skill, playerSkill.getXp(skill));
        }
    }

    public double getXp(Skill skill) {
        if (xp.containsKey(skill)) {
            return xp.get(skill);
        }
        else {
            return 0;
        }
    }

    public int getSkillLevel(Skill skill) {
        return levels.getOrDefault(skill, 0);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getPowerLevel() {
        int power = 0;
        for (int level : levels.values()) {
            power += level;
        }
        return power;
    }

    public double getPowerXp() {
        double powerXp = 0.0;
        for (double skillXp : xp.values()) {
            powerXp += skillXp;
        }
        return powerXp;
    }
}
