package com.archyx.aureliumskills.rewards;

public class RewardMessages {

    private final String menuMessage;
    private final String chatMessage;

    public RewardMessages(String menuMessage, String chatMessage) {
        this.menuMessage = menuMessage;
        this.chatMessage = chatMessage;
    }

    public String getMenuMessage() {
        return menuMessage;
    }

    public String getChatMessage() {
        return chatMessage;
    }

}
