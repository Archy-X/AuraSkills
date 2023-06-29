package dev.aurelium.auraskills.common.reward.builder;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;

public abstract class RewardBuilder {

    protected final AuraSkillsPlugin plugin;

    public RewardBuilder(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract SkillReward build();

}
