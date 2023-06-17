package dev.aurelium.auraskills.common.rewards.builder;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.rewards.Reward;

public abstract class RewardBuilder {

    protected final AuraSkillsPlugin plugin;

    public RewardBuilder(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract Reward build();

}
