package dev.aurelium.auraskills.common.reward.parser;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.util.data.Parser;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class RewardParser extends Parser {

    protected final AuraSkillsPlugin plugin;

    public RewardParser(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Nullable
    public abstract SkillReward parse(Map<?, ?> map);

}
