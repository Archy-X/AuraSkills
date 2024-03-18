package dev.aurelium.auraskills.common.reward;

import dev.aurelium.auraskills.common.reward.builder.*;
import dev.aurelium.auraskills.common.reward.parser.*;
import dev.aurelium.auraskills.common.reward.type.*;

public enum RewardType {

    STAT("stat", StatReward.class, StatRewardParser.class, StatRewardBuilder.class),
    COMMAND("command", CommandReward.class, CommandRewardParser.class, CommandRewardBuilder.class),
    PERMISSION("permission", PermissionReward.class, PermissionRewardParser.class, PermissionRewardBuilder.class),
    MONEY("money", MoneyReward.class, MoneyRewardParser.class, MoneyRewardBuilder.class),
    ITEM("item", ItemReward.class, ItemRewardParser.class, ItemRewardBuilder.class);

    private final String key;
    private final Class<? extends SkillReward> provider;
    private final Class<? extends RewardParser> parser;
    private final Class<? extends RewardBuilder> builder;

    RewardType(String key, Class<? extends SkillReward> provider, Class<? extends RewardParser> parser, Class<? extends RewardBuilder> builder) {
        this.key = key;
        this.provider = provider;
        this.parser = parser;
        this.builder = builder;
    }

    public String getKey() {
        return key;
    }

    public Class<? extends SkillReward> getProvider() {
        return provider;
    }

    public Class<? extends RewardParser> getParser() {
        return parser;
    }

    public Class<? extends RewardBuilder> getBuilder() {
        return builder;
    }

}
