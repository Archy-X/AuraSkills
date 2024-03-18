package dev.aurelium.auraskills.common.leaderboard;

import java.util.Comparator;

public class AverageSorter implements Comparator<SkillValue> {

    @Override
    public int compare(SkillValue a, SkillValue b) {
        return (int) (b.xp() * 100) - (int) (a.xp() * 100);
    }
}
