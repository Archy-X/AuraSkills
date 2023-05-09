package dev.auramc.auraskills.common.message.recipient;

import co.aikar.commands.CommandIssuer;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;
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
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(issuer.getUniqueId());
            if (playerData != null) {
                playerData.sendMessage(component);
            }
        } else { // Convert component to string if console
            issuer.sendMessage(LegacyComponentSerializer.legacySection().serialize(component));
        }
    }
}
