package dev.aurelium.skills.common.rewards.builder;

import dev.aurelium.skills.common.AureliumSkillsPlugin;

public abstract class MessagedRewardBuilder extends RewardBuilder {

    protected String menuMessage;
    protected String chatMessage;

    public MessagedRewardBuilder(AureliumSkillsPlugin plugin) {
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
