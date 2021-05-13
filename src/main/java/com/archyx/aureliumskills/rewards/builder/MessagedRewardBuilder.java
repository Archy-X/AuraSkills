package com.archyx.aureliumskills.rewards.builder;

import com.archyx.aureliumskills.AureliumSkills;

public abstract class MessagedRewardBuilder extends RewardBuilder {

    protected String menuMessage;
    protected String chatMessage;

    public MessagedRewardBuilder(AureliumSkills plugin) {
        super(plugin);
        this.menuMessage = "";
        this.chatMessage = "";
    }

    public MessagedRewardBuilder menuMessage(String menuMessage) {
        this.menuMessage = menuMessage;
        return this;
    }

    public MessagedRewardBuilder chatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
        return this;
    }

}
