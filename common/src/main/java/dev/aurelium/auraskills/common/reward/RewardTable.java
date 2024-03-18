package dev.aurelium.auraskills.common.reward;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.PermissionsHook;
import dev.aurelium.auraskills.common.reward.type.PermissionReward;
import dev.aurelium.auraskills.common.reward.type.StatReward;
import dev.aurelium.auraskills.common.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardTable {

    private final AuraSkillsPlugin plugin;
    private final List<Stat> statsLeveled;
    private final Map<Integer, List<SkillReward>> rewards;

    public RewardTable(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.rewards = new HashMap<>();
        this.statsLeveled = new ArrayList<>();
    }

    public ImmutableList<SkillReward> getRewards(int level) {
        return ImmutableList.copyOf(rewards.getOrDefault(level, new ArrayList<>()));
    }

    public Map<Integer, List<SkillReward>> getRewardsMap() {
        return rewards;
    }

    public void addReward(@NotNull SkillReward reward, int level) {
        List<SkillReward> rewards = this.rewards.computeIfAbsent(level, k -> new ArrayList<>());
        rewards.add(reward);
        if (reward instanceof StatReward statReward) {
            if (!statsLeveled.contains(statReward.getStat())) {
                statsLeveled.add(statReward.getStat());
            }
        }
    }

    public ImmutableList<Stat> getStatsLeveled() {
        return ImmutableList.copyOf(statsLeveled);
    }

    /**
     * Searches rewards for all rewards of a certain type
     * @param type The class of the type of reward to search
     * @param <T> The reward type
     * @return A map of each level to a list of rewards of that type
     */
    public <T extends SkillReward> Map<Integer, ImmutableList<T>> searchRewards(Class<T> type) {
        Map<Integer, ImmutableList<T>> rewardMap = new HashMap<>();
        for (Map.Entry<Integer, List<SkillReward>> entry : rewards.entrySet()) {
            List<T> rewardList = new ArrayList<>();
            for (SkillReward reward : entry.getValue()) {
                if (type.isInstance(reward)) {
                    rewardList.add(type.cast(reward));
                }
            }
            rewardMap.put(entry.getKey(), ImmutableList.copyOf(rewardList));
        }
        return rewardMap;
    }

    // Searches all rewards of a certain type at a single level
    public <T extends SkillReward> ImmutableList<T> searchRewards(Class<T> type, int level) {
        ImmutableList<SkillReward> levelRewards = getRewards(level);
        List<T> rewardList = new ArrayList<>();
        for (SkillReward reward : levelRewards) {
            if (type.isInstance(reward)) {
                rewardList.add(type.cast(reward));
            }
        }
        return ImmutableList.copyOf(rewardList);
    }

    public void applyStats(User user, int level) {
        Map<Integer, ImmutableList<StatReward>> statRewardMap = searchRewards(StatReward.class);
        for (int i = plugin.config().getStartLevel() + 1; i <= level; i++) {
            ImmutableList<StatReward> statRewardList = statRewardMap.get(i);
            if (statRewardList != null) {
                for (StatReward statReward : statRewardList) {
                    user.addStatLevel(statReward.getStat(), statReward.getValue());
                }
            }
        }
    }

    public Map<Stat, Double> applyStats(int level) {
        Map<Stat, Double> statsMap = new HashMap<>();
        Map<Integer, ImmutableList<StatReward>> statRewardMap = searchRewards(StatReward.class);
        for (int i = plugin.config().getStartLevel() + 1; i <= level; i++) {
            ImmutableList<StatReward> statRewardList = statRewardMap.get(i);
            if (statRewardList != null) {
                for (StatReward statReward : statRewardList) {
                    double existing = statsMap.getOrDefault(statReward.getStat(), 0.0);
                    statsMap.put(statReward.getStat(), existing + statReward.getValue());
                }
            }
        }
        return statsMap;
    }

    public void applyPermissions(User player, int level) {
        Map<Integer, ImmutableList<PermissionReward>> permissionRewardMap = searchRewards(PermissionReward.class);
        for (Map.Entry<Integer, ImmutableList<PermissionReward>> entry : permissionRewardMap.entrySet()) {
            int entryLevel = entry.getKey();
            for (PermissionReward reward : entry.getValue()) {
                if (plugin.getHookManager().isRegistered(PermissionsHook.class)) {
                    PermissionsHook hook = plugin.getHookManager().getHook(PermissionsHook.class);
                    if (level >= entryLevel) { // Add permission if unlocked
                        hook.setPermission(player, reward.getPermission(), reward.getValue());
                    } else { // Remove permission if not unlocked
                        hook.unsetPermission(player, reward.getPermission(), reward.getValue());
                    }
                }
            }
        }
    }

}
