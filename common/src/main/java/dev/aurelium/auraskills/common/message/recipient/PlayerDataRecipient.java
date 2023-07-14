package dev.aurelium.auraskills.common.message.recipient;

import dev.aurelium.auraskills.common.user.User;
import net.kyori.adventure.text.Component;

public class PlayerDataRecipient implements Recipient {

    private final User user;

    public PlayerDataRecipient(User user) {
        this.user = user;
    }

    @Override
    public void sendMessage(Component component) {
        user.sendMessage(component);
    }
}
