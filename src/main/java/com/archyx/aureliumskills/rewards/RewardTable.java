package com.archyx.aureliumskills.rewards;

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

}
