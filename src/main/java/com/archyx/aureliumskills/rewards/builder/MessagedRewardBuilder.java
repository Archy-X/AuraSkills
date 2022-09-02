package com.archyx.aureliumskills.rewards.builder;

import com.archyx.aureliumskills.AureliumSkills;
import org.jetbrains.annotations.NotNull;

public abstract class MessagedRewardBuilder extends RewardBuilder {

    protected String menuMessage;
    protected String chatMessage;

    public MessagedRewardBuilder(AureliumSkills plugin) {
        super(plugin);
        this.menuMessage = "";
        this.chatMessage = "";
    }

    public @NotNull MessagedRewardBuilder menuMessage(String menuMessage) {
        this.menuMessage = menuMessage;
        return this;
    }

    public @NotNull MessagedRewardBuilder chatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
        return this;
    }

}
