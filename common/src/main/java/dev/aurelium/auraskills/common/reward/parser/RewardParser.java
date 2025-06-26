package dev.aurelium.auraskills.common.reward.parser;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class RewardParser {

    protected final AuraSkillsPlugin plugin;
    protected final Skill skill;

    public RewardParser(AuraSkillsPlugin plugin, Skill skill) {
        this.plugin = plugin;
        this.skill = skill;
    }

    @Nullable
    public abstract SkillReward parse(ConfigurationNode config);

}
