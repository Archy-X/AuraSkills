package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.hooks.LuckPermsHook;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.Flag;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BukkitLuckPermsHook extends LuckPermsHook implements Listener {

    private final String prefix = "auraskills.multiplier.";
    private final Map<UUID, Set<String>> permissionCache = new ConcurrentHashMap<>();
    private final boolean usePermissionCache;

    public BukkitLuckPermsHook(AuraSkillsPlugin plugin, ConfigurationNode config) {
        super(plugin, config);

        this.usePermissionCache = config.node("use_permission_cache").getBoolean(true);

        if (!this.usePermissionCache) return;

        luckPerms.getEventBus().subscribe(NodeAddEvent.class,
                event -> handleEvent(event.getNode(), event.getTarget()));

        luckPerms.getEventBus().subscribe(NodeRemoveEvent.class,
                event -> handleEvent(event.getNode(), event.getTarget()));
    }

    public boolean usePermissionCache() {
        return usePermissionCache;
    }

    private void handleEvent(Node node, PermissionHolder target) {
        if (!(node instanceof PermissionNode) && !(node instanceof InheritanceNode)) return;

        if (node instanceof PermissionNode permissionNode) {
            if (!permissionNode.getValue()) return;
            if (!permissionNode.getPermission().startsWith(prefix)) return;
        }

        if (target instanceof User user) {
            plugin.getScheduler().scheduleAsync(
                    () -> {
                        Player player = Bukkit.getPlayer(user.getUniqueId());
                        // In case if someone logs out in that 500 ms timeframe
                        if (player == null || !player.isOnline()) return;
                        permissionCache.put(user.getUniqueId(), getMultiplierPermissions(user.getUniqueId()));
                    },
                    500,
                    TimeUnit.MILLISECONDS
            );
        } else if (target instanceof Group group) {
            final List<UUID> affectedPlayers = new ArrayList<>(permissionCache.keySet().size());

            if (node instanceof InheritanceNode) {
                // This shouldn't really happen on a prod server too often.
                affectedPlayers.addAll(permissionCache.keySet());
            } else {
                permissionCache.keySet().forEach((key) -> {
                    User user = luckPerms.getUserManager().getUser(key);
                    if (user == null) return;

                    if (user.getInheritedGroups(QueryOptions.builder(QueryMode.CONTEXTUAL)
                                    .context(QueryOptions.defaultContextualOptions().context())
                                    .flag(Flag.RESOLVE_INHERITANCE, true).build())
                            .stream().anyMatch((g) -> group.getName().equals(g.getName()))) {
                        affectedPlayers.add(user.getUniqueId());
                    }
                });
            }

            plugin.getScheduler().scheduleAsync(() -> {
                for (UUID uuid : affectedPlayers) {
                    Player player = Bukkit.getPlayer(uuid);
                    // In case if someone logs out in that 500 ms timeframe
                    if (player == null || !player.isOnline()) continue;
                    permissionCache.put(uuid, getMultiplierPermissions(uuid));
                }
            }, 500, TimeUnit.MILLISECONDS);
        }
    }

    public Set<String> getMultiplierPermissions(Player player) {
        return permissionCache.computeIfAbsent(player.getUniqueId(), this::getMultiplierPermissions);
    }

    private Set<String> getMultiplierPermissions(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return new HashSet<>();
        }

        return player.getEffectivePermissions()
                .stream()
                .filter(PermissionAttachmentInfo::getValue)
                .map(PermissionAttachmentInfo::getPermission)
                .filter(p -> p.startsWith(prefix))
                .collect(Collectors.toSet());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Put it in the cache async
        UUID uuid = event.getPlayer().getUniqueId();
        plugin.getScheduler().executeAsync(() -> {
            if (!event.getPlayer().isOnline()) return;
            permissionCache.put(uuid, getMultiplierPermissions(uuid));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        permissionCache.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return BukkitLuckPermsHook.class;
    }
}
