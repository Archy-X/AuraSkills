package com.archyx.aureliumskills.leaderboard;

import java.util.Comparator;

public class LeaderboardSorter implements Comparator<SkillValue>{

    @Override
    public int compare(SkillValue a, SkillValue b) {
        int levelA = a.getLevel();
        int levelB = b.getLevel();
        if (levelA != levelB) {
            return levelB - levelA;
        }
        else {
            return (int) (b.getXp() * 100) - (int) (a.getXp() * 100);
        }
    }

}
