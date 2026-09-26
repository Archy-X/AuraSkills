package dev.aurelium.auraskills.bukkit.hooks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.ui.ActionBarManager;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.concurrent.TimeUnit;

public class ProtocolLibHook extends Hook {

    private static final int PAUSE_MILLIS = 2500;

    private final AuraSkills skillsPlugin;
    private final ProtocolManager protocolManager;
    private volatile boolean enabled = true;

    public ProtocolLibHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.skillsPlugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        registerListeners();
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return ProtocolLibHook.class;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void sendActionBar(Player player, String message) {
        try {
            if (VersionUtils.isAtLeastVersion(17)) {
                sendActionBarTextPacket(player, message);
            } else {
                sendTitlePacket(player, message);
            }
        } catch (Exception e) {
            disableAndPrint(e, "send action bar");
        }
    }

    private void disableAndPrint(Exception e, String cause) {
        if (enabled) {
            this.enabled = false;
            plugin.logger().severe("Failed to " + cause + " with ProtocolLib, disabling ProtocolLib support");
            e.printStackTrace();
        }
    }

    private void sendActionBarTextPacket(Player player, String message) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        packet.getChatComponents().write(0, WrappedChatComponent.fromLegacyText(message));
        packet.setMeta("AuraSkills", true); // Mark packet as from Aurelium Skills
        protocolManager.sendServerPacket(player, packet);
    }

    @SuppressWarnings("deprecation")
    private void sendTitlePacket(Player player, String message) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.TITLE);
        packet.getEnumModifier(EnumWrappers.TitleAction.class, 0).write(0, EnumWrappers.TitleAction.ACTIONBAR);
        packet.getChatComponents().write(0, WrappedChatComponent.fromLegacyText(message));
        packet.setMeta("AuraSkills", true); // Mark packet as from Aurelium Skills
        protocolManager.sendServerPacket(player, packet);
    }

    public void registerListeners() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        if (VersionUtils.isAtLeastVersion(17)) {
            registerNewListener(manager);
        } else {
            registerLegacyListener(manager);
        }
        if (VersionUtils.isAtLeastVersion(19)) {
            registerSystemChatListener(manager);
        }
        registerChatListener(manager);
    }

    private User getUser(Player player) {
        return skillsPlugin.getUser(player);
    }

    private ActionBarManager getActionBar() {
        return skillsPlugin.getUiProvider().getActionBarManager();
    }

    private void registerNewListener(ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(skillsPlugin, ListenerPriority.MONITOR, PacketType.Play.Server.SET_ACTION_BAR_TEXT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!enabled) return;

                try {
                    if (event.isPlayerTemporary()) return;

                    Player player = event.getPlayer();
                    PacketContainer packet = event.getPacket();
                    if (packet.getMeta("AuraSkills").isPresent()) return; // Ignore AuraSkills action bars

                    getActionBar().setPaused(getUser(player), PAUSE_MILLIS, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    disableAndPrint(e, "handle SET_ACTION_BAR_TEXT PacketEvent");
                }
            }
        });
    }

    private void registerSystemChatListener(ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(skillsPlugin, ListenerPriority.MONITOR, PacketType.Play.Server.SYSTEM_CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!enabled) return;

                try {
                    if (event.isPlayerTemporary()) return;

                    Player player = event.getPlayer();
                    PacketContainer packet = event.getPacket();
                    if (packet.getMeta("AuraSkills").isPresent()) return;
                    StructureModifier<Integer> integers = packet.getIntegers();
                    if (integers.size() == 1) {
                        if (integers.read(0) == EnumWrappers.ChatType.GAME_INFO.getId()) {
                            getActionBar().setPaused(getUser(player), PAUSE_MILLIS, TimeUnit.MILLISECONDS);
                        }
                    } else if (packet.getBooleans().read(0)) {
                        getActionBar().setPaused(getUser(player), PAUSE_MILLIS, TimeUnit.MILLISECONDS);
                    }
                } catch (Exception e) {
                    disableAndPrint(e, "handle SYSTEM_CHAT PacketEvent");
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void registerLegacyListener(ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(skillsPlugin, ListenerPriority.MONITOR, PacketType.Play.Server.TITLE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!enabled) return;

                try {
                    if (event.isPlayerTemporary()) return;

                    Player player = event.getPlayer();
                    PacketContainer packet = event.getPacket();
                    // Make sure the title packet is for the action bar
                    if (packet.getEnumModifier(EnumWrappers.TitleAction.class, 0).read(0) != EnumWrappers.TitleAction.ACTIONBAR) {
                        return;
                    }
                    if (packet.getMeta("AuraSkills").isPresent()) return; // Ignore Aurelium Skills action bars
                    getActionBar().setPaused(getUser(player), PAUSE_MILLIS, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    disableAndPrint(e, "handle TITLE PacketEvent");
                }
            }
        });
    }

    private void registerChatListener(ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(skillsPlugin, ListenerPriority.MONITOR, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!enabled) return;

                try {
                    if (event.isPlayerTemporary()) return;

                    Player player = event.getPlayer();
                    PacketContainer packet = event.getPacket();
                    // Make sure the chat packet is for the action bar
                    if (packet.getChatTypes().read(0) != EnumWrappers.ChatType.GAME_INFO) {
                        return;
                    }
                    if (packet.getMeta("AuraSkills").isPresent()) return;
                    getActionBar().setPaused(getUser(player), PAUSE_MILLIS, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    disableAndPrint(e, "handle CHAT PacketEvent");
                }
            }
        });
    }

}
