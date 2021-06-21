package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.AureliumSkills;

public abstract class Loot {

    protected final AureliumSkills plugin;
    protected final int weight;
    protected final String message;
    protected final double xp;

    public Loot(AureliumSkills plugin, int weight, String message, double xp) {
        this.plugin = plugin;
        this.weight = weight;
        this.message = message;
        this.xp = xp;
    }

    public int getWeight() {
        return weight;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Gets the amount of Skill XP to reward
     * @return The amount of XP, -1 if not specified
     */
    public double getXp() {
        return xp;
    }

}
