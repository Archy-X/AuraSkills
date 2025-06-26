package dev.aurelium.auraskills.bukkit.user;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.ref.PlayerRef;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dev.aurelium.auraskills.bukkit.ref.BukkitPlayerRef.unwrap;
import static dev.aurelium.auraskills.bukkit.ref.BukkitPlayerRef.wrap;

public class BukkitUserManager extends UserManager {

    private final AuraSkills plugin;

    public BukkitUserManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @NotNull
    public User getUser(Player player) {
        User user = playerDataMap.get(player.getUniqueId());
        if (user != null) {
            return user;
        } else {
            return createNewUser(player.getUniqueId(), wrap(player));
        }
    }

    @Override
    public User instantiateUser(UUID uuid, PlayerRef ref) {
        return new BukkitUser(uuid, ref != null ? unwrap(ref) : null, plugin);
    }

    @Override
    public List<User> getOnlineUsers() {
        List<User> online = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = playerDataMap.get(player.getUniqueId());
            if (user != null) {
                online.add(user);
            }
        }
        return online;
    }

}
