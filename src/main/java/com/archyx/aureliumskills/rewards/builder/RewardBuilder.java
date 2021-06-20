package com.archyx.aureliumskills.rewards.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.rewards.Reward;

public abstract class RewardBuilder {

    protected final AureliumSkills plugin;

    public RewardBuilder(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public abstract Reward build();

}
