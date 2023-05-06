package dev.auramc.auraskills.common.rewards.builder;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.rewards.Reward;

public abstract class RewardBuilder {

    protected final AuraSkillsPlugin plugin;

    public RewardBuilder(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract Reward build();

}
