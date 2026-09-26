package dev.aurelium.auraskills.bukkit.hooks;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerActionBar;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.ui.ActionBarManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.concurrent.TimeUnit;

public class PacketEventsHook extends Hook {

    private static final int PAUSE_MILLIS = 2500;

    private final AuraSkills plugin;
    private volatile boolean enabled = true;

    public PacketEventsHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
        EventManager events = PacketEvents.getAPI().getEventManager();
        events.registerListener(new AuraSkillsPacketListener(), PacketListenerPriority.MONITOR);
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return PacketEventsHook.class;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void sendActionBar(Player player, Component component) {
        if (component == null || !player.isOnline()) return;

        try {
            WrapperPlayServerActionBar packet = new WrapperPlayServerActionBar(component);

            PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, packet);
        } catch (Exception e) {
            if (enabled) {
                this.enabled = false;
                plugin.logger().severe("Failed to send action bar with packetevents, disabling packetevents support");
                e.printStackTrace();
            }
        }
    }

    private ActionBarManager getActionBar() {
        return plugin.getUiProvider().getActionBarManager();
    }

    class AuraSkillsPacketListener implements PacketListener {

        @Override
        public void onPacketSend(PacketSendEvent event) {
            if (!enabled) return;

            try {
                if (event.getPacketType() == Server.SYSTEM_CHAT_MESSAGE) {
                    Object playerObj = event.getPlayer();
                    if (playerObj instanceof Player player) {
                        var packet = new WrapperPlayServerSystemChatMessage(event);

                        if (packet.isOverlay()) {
                            getActionBar().setPaused(plugin.getUser(player), PAUSE_MILLIS, TimeUnit.MILLISECONDS);
                        }
                    }
                } else if (event.getPacketType() == Server.ACTION_BAR) {
                    Object playerObj = event.getPlayer();
                    if (playerObj instanceof Player player) {
                        getActionBar().setPaused(plugin.getUser(player), PAUSE_MILLIS, TimeUnit.MILLISECONDS);
                    }
                }
            } catch (Exception e) {
                if (enabled) {
                    enabled = false;
                    plugin.logger().severe("Failed to handle packet send with packetevents, disabling packetevents support.");
                    e.printStackTrace();
                }
            }
        }
    }
}
