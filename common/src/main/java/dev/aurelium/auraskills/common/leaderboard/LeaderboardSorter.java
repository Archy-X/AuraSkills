package dev.aurelium.auraskills.common.leaderboard;

import java.util.Comparator;

public class LeaderboardSorter implements Comparator<SkillValue>{

    @Override
    public int compare(SkillValue a, SkillValue b) {
        int levelA = a.level();
        int levelB = b.level();
        if (levelA != levelB) {
            return levelB - levelA;
        }
        else {
            return (int) (b.xp() * 100) - (int) (a.xp() * 100);
        }
    }

}
