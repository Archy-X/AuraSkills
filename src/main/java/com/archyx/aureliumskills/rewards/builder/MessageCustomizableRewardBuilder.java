package com.archyx.aureliumskills.rewards.builder;

import com.archyx.aureliumskills.AureliumSkills;

public abstract class MessageCustomizableRewardBuilder extends RewardBuilder {

    protected String menuMessage;
    protected String chatMessage;

    public MessageCustomizableRewardBuilder(AureliumSkills plugin) {
        super(plugin);
        this.menuMessage = "";
        this.chatMessage = "";
    }

    public MessageCustomizableRewardBuilder menuMessage(String menuMessage) {
        this.menuMessage = menuMessage;
        return this;
    }

    public MessageCustomizableRewardBuilder chatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
        return this;
    }

}
