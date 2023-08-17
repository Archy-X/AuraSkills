package dev.aurelium.auraskills.bukkit.hooks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.Hook;
import org.bukkit.entity.Player;

public class ProtocolLibHook extends Hook {

    private final ProtocolManager protocolManager;

    public ProtocolLibHook(AuraSkillsPlugin plugin) {
        super(plugin);
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void sendActionBar(Player player, String message) {
        if (VersionUtils.isAtLeastVersion(17)) {
            sendActionBarTextPacket(player, message);
        } else {
            sendTitlePacket(player, message);
        }
    }

    private void sendActionBarTextPacket(Player player, String message) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        packet.setMeta("AuraSkills", true); // Mark packet as from Aurelium Skills
        protocolManager.sendServerPacket(player, packet);
    }

    @SuppressWarnings("deprecation")
    private void sendTitlePacket(Player player, String message) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.TITLE);
        packet.getEnumModifier(EnumWrappers.TitleAction.class, 0).write(0, EnumWrappers.TitleAction.ACTIONBAR);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        packet.setMeta("AuraSkills", true); // Mark packet as from Aurelium Skills
        protocolManager.sendServerPacket(player, packet);
    }

}
