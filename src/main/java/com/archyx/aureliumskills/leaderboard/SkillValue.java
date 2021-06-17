package com.archyx.aureliumskills.leaderboard;

import java.util.UUID;

public class SkillValue {

    private final UUID id;
    private final int level;
    private final double xp;

    public SkillValue(UUID id, int level, double xp) {
        this.id = id;
        this.level = level;
        this.xp = xp;
    }

    public UUID getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public double getXp() {
        return xp;
    }

}
