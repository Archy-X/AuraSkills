package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.hooks.LuckPermsHook;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.Flag;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BukkitLuckPermsHook extends LuckPermsHook {
    private final Map<UUID, Set<PermissionAttachmentInfo>> permissionCache = new ConcurrentHashMap<>();

    public BukkitLuckPermsHook(AuraSkillsPlugin plugin, ConfigurationNode config) {
        super(plugin, config);

        // This will run async saving main thread resources
        luckPerms.getEventBus().subscribe(NodeMutateEvent.class, (event) -> {
            if (event.isUser() && event.getTarget() instanceof User user) {
                permissionCache.put(user.getUniqueId(), getMultiplierPermissions(user.getUniqueId()));
            }

            if (event.isGroup()) {
                // Effective perms won't update instantly
                plugin.getScheduler().scheduleAsync(() -> {
                    permissionCache.keySet().forEach(key -> permissionCache.put(key, getMultiplierPermissions(key)));
                }, 200, TimeUnit.MILLISECONDS);
            }
        });
    }

    public Set<PermissionAttachmentInfo> getMultiplierPermissions(Player player) {
        return permissionCache.computeIfAbsent(
                player.getUniqueId(),
                (uuid -> getMultiplierPermissions(player.getUniqueId())));
    }

    private Set<PermissionAttachmentInfo> getMultiplierPermissions(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return new HashSet<>();
        }

        Set<PermissionAttachmentInfo> filteredPermissions = new HashSet<>();

        for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
            String permission = pai.getPermission();
            if (permission.startsWith("auraskills.multiplier.")) {
                filteredPermissions.add(pai);
            }
        }

        return filteredPermissions;
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return BukkitLuckPermsHook.class;
    }
}
