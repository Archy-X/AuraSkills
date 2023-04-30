package dev.aurelium.skills.common.rewards.parser;

import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.rewards.Reward;
import dev.aurelium.skills.common.util.data.Parser;

import java.util.Map;

public abstract class RewardParser extends Parser {

    protected final AureliumSkillsPlugin plugin;

    public RewardParser(AureliumSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract Reward parse(Map<?, ?> map);

}
