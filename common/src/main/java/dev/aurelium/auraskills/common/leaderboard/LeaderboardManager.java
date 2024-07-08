package dev.aurelium.auraskills.common.leaderboard;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserState;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LeaderboardManager {

    private final AuraSkillsPlugin plugin;
    private final Map<Skill, List<SkillValue>> skillLeaderboards;
    private List<SkillValue> powerLeaderboard;
    private List<SkillValue> averageLeaderboard;
    private volatile boolean sorting = false;

    public LeaderboardManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.skillLeaderboards = new HashMap<>();
        this.powerLeaderboard = new ArrayList<>();
        this.averageLeaderboard = new ArrayList<>();
    }

    public void startLeaderboardUpdater() {
        plugin.getScheduler().timerAsync(new TaskRunnable() {
            @Override
            public void run() {
                updateLeaderboardsSync();
            }
        }, 5 * 60, 5 * 60, TimeUnit.SECONDS);
    }

    public void updateLeaderboards() {
        updateLeaderboardsSync();
    }

    private void updateLeaderboardsSync() {
        if (sorting) return;
        try {
            setSorting(true);
            // Initialize lists
            Map<Skill, List<SkillValue>> skillLeaderboards = new HashMap<>();
            for (Skill skill : plugin.getSkillRegistry().getValues()) {
                skillLeaderboards.put(skill, new ArrayList<>());
            }
            List<SkillValue> powerLeaderboard = new ArrayList<>();
            List<SkillValue> averageLeaderboard = new ArrayList<>();
            // Add players already in memory
            addLoadedPlayersToLeaderboards(skillLeaderboards, powerLeaderboard, averageLeaderboard);
            // Add offline players
            addOfflinePlayers(skillLeaderboards, powerLeaderboard, averageLeaderboard);
            // Sort leaderboards and set as current
            sortLeaderboards(skillLeaderboards, powerLeaderboard, averageLeaderboard);

            setSorting(false);
        } catch (Exception e) {
            plugin.logger().warn("Error updating leaderboards: " + e.getMessage());
            e.printStackTrace();
            setSorting(false);
        }
    }

    public List<SkillValue> getLeaderboard(Skill skill) {
        return skillLeaderboards.computeIfAbsent(skill, s -> new ArrayList<>());
    }

    public void setLeaderboard(Skill skill, List<SkillValue> leaderboard) {
        this.skillLeaderboards.put(skill, leaderboard);
    }

    public List<SkillValue> getLeaderboard(Skill skill, int page, int numPerPage) {
        List<SkillValue> leaderboard = skillLeaderboards.computeIfAbsent(skill, k -> new ArrayList<>());
        int from = (Math.max(page, 1) - 1) * numPerPage;
        int to = from + numPerPage;
        return leaderboard.subList(Math.min(from, leaderboard.size()), Math.min(to, leaderboard.size()));
    }

    @Nullable
    public SkillValue getSkillValue(Skill skill, int place) {
        List<SkillValue> values = getLeaderboard(skill, place, 1);
        if (!values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    public List<SkillValue> getPowerLeaderboard() {
        return powerLeaderboard;
    }

    public List<SkillValue> getPowerLeaderboard(int page, int numPerPage) {
        int from = (Math.max(page, 1) - 1) * numPerPage;
        int to = from + numPerPage;
        return powerLeaderboard.subList(Math.min(from, powerLeaderboard.size()), Math.min(to, powerLeaderboard.size()));
    }

    public void setPowerLeaderboard(List<SkillValue> leaderboard) {
        this.powerLeaderboard = leaderboard;
    }

    public List<SkillValue> getAverageLeaderboard() {
        return averageLeaderboard;
    }

    public List<SkillValue> getAverageLeaderboard(int page, int numPerPage) {
        int from = (Math.max(page, 1) - 1) * numPerPage;
        int to = from + numPerPage;
        return averageLeaderboard.subList(Math.min(from, averageLeaderboard.size()), Math.min(to, averageLeaderboard.size()));
    }

    public void setAverageLeaderboard(List<SkillValue> leaderboard) {
        this.averageLeaderboard = leaderboard;
    }

    public int getSkillRank(Skill skill, UUID id) {
        List<SkillValue> leaderboard = skillLeaderboards.get(skill);
        if (leaderboard == null) return 0;

        for (SkillValue skillValue : leaderboard) {
            if (skillValue.id().equals(id)) {
                return leaderboard.indexOf(skillValue) + 1;
            }
        }
        return 0;
    }

    public int getPowerRank(UUID id) {
        for (SkillValue skillValue : powerLeaderboard) {
            if (skillValue.id().equals(id)) {
                return powerLeaderboard.indexOf(skillValue) + 1;
            }
        }
        return 0;
    }

    public int getAverageRank(UUID id) {
        for (SkillValue skillValue : averageLeaderboard) {
            if (skillValue.id().equals(id)) {
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

    private void addLoadedPlayersToLeaderboards(Map<Skill, List<SkillValue>> skillLb, List<SkillValue> powerLb, List<SkillValue> averageLb) {
        for (User user : plugin.getUserManager().getUserMap().values()) {
            UUID id = user.getUuid();
            int powerLevel = 0;
            double powerXp = 0;
            int numEnabled = 0;
            for (Skill skill : plugin.getSkillManager().getSkillValues()) {
                int level = user.getSkillLevel(skill);
                double xp = user.getSkillXp(skill);
                // Add to lists
                SkillValue skillLevel = new SkillValue(id, level, xp);
                skillLb.get(skill).add(skillLevel);

                if (skill.isEnabled()) {
                    powerLevel += level;
                    powerXp += xp;
                    numEnabled++;
                }
            }
            // Add power and average
            SkillValue powerValue = new SkillValue(id, powerLevel, powerXp);
            powerLb.add(powerValue);
            double averageLevel = (double) powerLevel / numEnabled;
            SkillValue averageValue = new SkillValue(id, 0, averageLevel);
            averageLb.add(averageValue);
        }
    }

    private void addOfflinePlayers(Map<Skill, List<SkillValue>> skillLb, List<SkillValue> powerLb, List<SkillValue> averageLb) throws Exception {
        List<UserState> offlineStates = plugin.getStorageProvider().loadStates(true, true);
        for (UserState state : offlineStates) {
            int powerLevel = 0;
            double powerXp = 0.0;
            int numEnabled = 0;
            for (Skill skill : state.skillLevels().keySet()) {
                int level = state.skillLevels().get(skill);
                double xp = state.skillXp().get(skill);

                // Add to skill leaderboard
                SkillValue skillValue = new SkillValue(state.uuid(), level, xp);
                skillLb.computeIfAbsent(skill, k -> new ArrayList<>()).add(skillValue);

                if (skill.isEnabled()) {
                    powerLevel += level;
                    powerXp += xp;
                    numEnabled++;
                }
            }
            // Add to power and average leaderboards
            powerLb.add(new SkillValue(state.uuid(), powerLevel, powerXp));

            double averageLevel = (double) powerLevel / numEnabled;
            averageLb.add(new SkillValue(state.uuid(), 0, averageLevel));
        }
    }

    private void sortLeaderboards(Map<Skill, List<SkillValue>> skillLb, List<SkillValue> powerLb, List<SkillValue> averageLb) {
        LeaderboardSorter sorter = new LeaderboardSorter();
        for (Skill skill : plugin.getSkillManager().getSkillValues()) {
            skillLb.computeIfAbsent(skill, k -> new ArrayList<>()).sort(sorter);
        }
        powerLb.sort(sorter);
        AverageSorter averageSorter = new AverageSorter();
        averageLb.sort(averageSorter);

        // Add skill leaderboards to map
        for (Skill skill : plugin.getSkillManager().getSkillValues()) {
            setLeaderboard(skill, skillLb.get(skill));
        }
        setPowerLeaderboard(powerLb);
        setAverageLeaderboard(averageLb);
    }

}
