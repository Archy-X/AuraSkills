package com.archyx.aureliumskills.rewards.builder;

import com.archyx.aureliumskills.AureliumSkills;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MessagedRewardBuilder extends RewardBuilder {

    protected @Nullable String menuMessage;
    protected @Nullable String chatMessage;

    public MessagedRewardBuilder(@NotNull AureliumSkills plugin) {
        super(plugin);
        this.menuMessage = "";
        this.chatMessage = "";
    }

    public @NotNull MessagedRewardBuilder menuMessage(@Nullable String menuMessage) {
        this.menuMessage = menuMessage;
        return this;
    }

    public @NotNull MessagedRewardBuilder chatMessage(@Nullable String chatMessage) {
        this.chatMessage = chatMessage;
        return this;
    }

}
