package dev.aurelium.auraskills.common.message.recipient;

import co.aikar.commands.CommandIssuer;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CommandIssuerRecipient implements Recipient {

    private final AuraSkillsPlugin plugin;
    private final CommandIssuer issuer;

    public CommandIssuerRecipient(AuraSkillsPlugin plugin, CommandIssuer issuer) {
        this.plugin = plugin;
        this.issuer = issuer;
    }

    @Override
    public void sendMessage(Component component) {
        if (issuer.isPlayer()) { // Send component if player
            User user = plugin.getUserManager().getUser(issuer.getUniqueId());
            if (user != null) {
                user.sendMessage(component);
            }
        } else { // Convert component to string if console
            issuer.sendMessage(LegacyComponentSerializer.legacySection().serialize(component));
        }
    }
}
