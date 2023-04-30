package dev.aurelium.skills.common.rewards.builder;

import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.rewards.Reward;

public abstract class RewardBuilder {

    protected final AureliumSkillsPlugin plugin;

    public RewardBuilder(AureliumSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract Reward build();

}
