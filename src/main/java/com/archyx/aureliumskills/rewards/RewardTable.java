package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.data.PlayerData;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardTable {

    private final Map<Integer, List<Reward>> rewards;

    public RewardTable() {
        this.rewards = new HashMap<>();
    }

    public ImmutableList<Reward> getRewards(int level) {
        return ImmutableList.copyOf(rewards.getOrDefault(level, new ArrayList<>()));
    }

    public void addReward(Reward reward, int level) {
        List<Reward> rewards = this.rewards.computeIfAbsent(level, k -> new ArrayList<>());
        rewards.add(reward);
    }

    public void setRewards(int level, List<Reward> rewards) {
        this.rewards.put(level, rewards);
    }

    /**
     * Searches rewards for all rewards of a certain type
     * @param type The class of the type of reward to search
     * @param <T> The reward type
     * @return A map of each level to a list of rewards of that type
     */
    public <T extends Reward> Map<Integer, ImmutableList<T>> searchRewards(Class<T> type) {
        Map<Integer, ImmutableList<T>> rewardMap = new HashMap<>();
        for (Map.Entry<Integer, List<Reward>> entry : rewards.entrySet()) {
            List<T> rewardList = new ArrayList<>();
            for (Reward reward : entry.getValue()) {
                if (type.isInstance(reward)) {
                    rewardList.add(type.cast(reward));
                }
            }
            rewardMap.put(entry.getKey(), ImmutableList.copyOf(rewardList));
        }
        return rewardMap;
    }

    public void addStatRewards(PlayerData playerData, int level) {
        Map<Integer, ImmutableList<StatReward>> statRewardMap = searchRewards(StatReward.class);
        for (int i = 2; i <= level; i++) {
            ImmutableList<StatReward> statRewardList = statRewardMap.get(i);
            for (StatReward statReward : statRewardList) {
                playerData.addStatLevel(statReward.getStat(), statReward.getValue());
            }
        }
    }

}
