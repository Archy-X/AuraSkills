package com.archyx.aureliumskills.support;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

public class ProtocolLibSupport {

    private static ProtocolManager protocolManager;

    public ProtocolLibSupport() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void sendNewActionBar(Player player, String message) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        packet.setMeta("AureliumSkills", true); // Mark packet as from Aurelium Skills
        protocolManager.sendServerPacket(player, packet);
    }

    public void sendLegacyActionBar(Player player, String message) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.TITLE);
        packet.getEnumModifier(EnumWrappers.TitleAction.class, 0).write(0, EnumWrappers.TitleAction.ACTIONBAR);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        packet.setMeta("AureliumSkills", true); // Mark packet as from Aurelium Skills
        protocolManager.sendServerPacket(player, packet);
    }

}
