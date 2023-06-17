package dev.aurelium.auraskills.common.message.recipient;

import dev.aurelium.auraskills.common.data.PlayerData;
import net.kyori.adventure.text.Component;

public class PlayerDataRecipient implements Recipient {

    private final PlayerData playerData;

    public PlayerDataRecipient(PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public void sendMessage(Component component) {
        playerData.sendMessage(component);
    }
}
