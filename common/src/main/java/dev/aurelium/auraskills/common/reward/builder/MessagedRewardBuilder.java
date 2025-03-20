package dev.aurelium.auraskills.common.reward.builder;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;

public abstract class MessagedRewardBuilder extends RewardBuilder {

    protected String menuMessage;
    protected String chatMessage;

    public MessagedRewardBuilder(AuraSkillsPlugin plugin) {
        super(plugin);
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
