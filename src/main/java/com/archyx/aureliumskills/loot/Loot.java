package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.source.Source;

import java.util.List;

public abstract class Loot {

    protected final AureliumSkills plugin;
    protected final int weight;
    protected final String message;
    protected final double xp;
    protected final List<Source> sources;

    public Loot(AureliumSkills plugin, int weight, String message, double xp, List<Source> sources) {
        this.plugin = plugin;
        this.weight = weight;
        this.message = message;
        this.xp = xp;
        this.sources = sources;
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

    public List<Source> getSources() {
        return sources;
    }

}
