package dev.aurelium.auraskills.common.reward.parser;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.util.data.Parser;

import java.util.Map;

public abstract class RewardParser extends Parser {

    protected final AuraSkillsPlugin plugin;

    public RewardParser(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract SkillReward parse(Map<?, ?> map);

}
