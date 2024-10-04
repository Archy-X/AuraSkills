package dev.aurelium.auraskills.common.reward.builder;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;

public abstract class RewardBuilder {

    protected final AuraSkillsPlugin plugin;
    protected Skill skill;

    public RewardBuilder(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public RewardBuilder skill(Skill skill) {
        this.skill = skill;
        return this;
    }

    public abstract SkillReward build();

}
