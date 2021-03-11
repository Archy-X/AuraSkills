package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.skills.Skill;

import java.util.HashMap;
import java.util.Map;

public class RewardManager {

    private final Map<Skill, RewardTable> rewardTables;

    public RewardManager() {
        this.rewardTables = new HashMap<>();
    }

    public RewardTable getRewardTable(Skill skill) {
        return rewardTables.get(skill);
    }

    public void loadRewards() {
        this.rewardTables.clear();
    }

}
