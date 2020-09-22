package com.archyx.aureliumskills.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ProtocolUtil {

    private static ProtocolManager protocolManager;

    public static void init() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public static void sendActionBar(Player player, String message) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.TITLE);
        packet.getEnumModifier(EnumWrappers.TitleAction.class, 0).write(0, EnumWrappers.TitleAction.ACTIONBAR);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet " + packet, e);
        }
    }
}
