package com.archyx.aureliumskills.leaderboard;

import java.util.Comparator;

public class AverageSorter implements Comparator<SkillValue> {

    @Override
    public int compare(SkillValue a, SkillValue b) {
        return (int) (b.getXp() * 100) - (int) (a.getXp() * 100);
    }
}
