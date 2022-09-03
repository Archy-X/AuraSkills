package com.archyx.aureliumskills.leaderboard;

import com.archyx.aureliumskills.skills.Skill;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LeaderboardManager {

    private final Map<@NotNull Skill, @NotNull List<@NotNull SkillValue>> skillLeaderboards;
    private @NotNull List<@NotNull SkillValue> powerLeaderboard;
    private @NotNull List<@NotNull SkillValue> averageLeaderboard;
    private volatile boolean sorting = false;

    public LeaderboardManager() {
        this.skillLeaderboards = new HashMap<>();
        this.powerLeaderboard = new ArrayList<>();
        this.averageLeaderboard = new ArrayList<>();
    }

    public @NotNull List<@NotNull SkillValue> getLeaderboard(@NotNull Skill skill) {
        return skillLeaderboards.get(skill);
    }

    public void setLeaderboard(@NotNull Skill skill, @NotNull List<@NotNull SkillValue> leaderboard) {
        this.skillLeaderboards.put(skill, leaderboard);
    }

    public @NotNull List<@NotNull SkillValue> getLeaderboard(@NotNull Skill skill, int page, int numPerPage) {
        List<@NotNull SkillValue> leaderboard = skillLeaderboards.get(skill);
        int from = (Math.max(page, 1) - 1) * numPerPage;
        int to = from + numPerPage;
        return leaderboard.subList(Math.min(from, leaderboard.size()), Math.min(to, leaderboard.size()));
    }

    public @NotNull List<@NotNull SkillValue> getPowerLeaderboard() {
        return powerLeaderboard;
    }

    public @NotNull List<@NotNull SkillValue> getPowerLeaderboard(int page, int numPerPage) {
        int from = (Math.max(page, 1) - 1) * numPerPage;
        int to = from + numPerPage;
        return powerLeaderboard.subList(Math.min(from, powerLeaderboard.size()), Math.min(to, powerLeaderboard.size()));
    }

    public void setPowerLeaderboard(@NotNull List<@NotNull SkillValue> leaderboard) {
        this.powerLeaderboard = leaderboard;
    }

    public @NotNull List<@NotNull SkillValue> getAverageLeaderboard() {
        return averageLeaderboard;
    }

    public @NotNull List<@NotNull SkillValue> getAverageLeaderboard(int page, int numPerPage) {
        int from = (Math.max(page, 1) - 1) * numPerPage;
        int to = from + numPerPage;
        return averageLeaderboard.subList(Math.min(from, averageLeaderboard.size()), Math.min(to, averageLeaderboard.size()));
    }

    public void setAverageLeaderboard(@NotNull List<@NotNull SkillValue> leaderboard) {
        this.averageLeaderboard = leaderboard;
    }

    public int getSkillRank(@NotNull Skill skill, @NotNull UUID id) {
        List<@NotNull SkillValue> leaderboard = skillLeaderboards.get(skill);
        for (SkillValue skillValue : leaderboard) {
            if (skillValue.getId().equals(id)) {
                return leaderboard.indexOf(skillValue) + 1;
            }
        }
        return 0;
    }

    public int getPowerRank(UUID id) {
        for (SkillValue skillValue : powerLeaderboard) {
            if (skillValue.getId().equals(id)) {
                return powerLeaderboard.indexOf(skillValue) + 1;
            }
        }
        return 0;
    }

    public int getAverageRank(UUID id) {
        for (SkillValue skillValue : averageLeaderboard) {
            if (skillValue.getId().equals(id)) {
                return averageLeaderboard.indexOf(skillValue) + 1;
            }
        }
        return 0;
    }

    public boolean isNotSorting() {
        return !sorting;
    }

    public void setSorting(boolean sorting) {
        this.sorting = sorting;
    }

}
