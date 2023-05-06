package dev.auramc.auraskills.common.rewards.parser;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.rewards.Reward;
import dev.auramc.auraskills.common.util.data.Parser;

import java.util.Map;

public abstract class RewardParser extends Parser {

    protected final AuraSkillsPlugin plugin;

    public RewardParser(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract Reward parse(Map<?, ?> map);

}
