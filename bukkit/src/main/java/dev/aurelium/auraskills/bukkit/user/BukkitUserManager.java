package dev.aurelium.auraskills.bukkit.user;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skill.LoadedSkill;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitUserManager implements UserManager {

    private final AuraSkills plugin;
    private final Map<UUID, User> playerDataMap = new ConcurrentHashMap<>();

    public BukkitUserManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public User getUser(Player player) {
        User user = playerDataMap.get(player.getUniqueId());
        if (user != null) {
            return user;
        } else {
            return createNewUser(player.getUniqueId());
        }
    }

    @Override
    @Nullable
    public User getUser(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    @Override
    public void addUser(User user) {
        playerDataMap.put(user.getUuid(), user);
    }

    @Override
    public void removeUser(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    @Override
    public boolean hasUser(UUID uuid) {
        return playerDataMap.containsKey(uuid);
    }

    @Override
    public Map<UUID, User> getUserMap() {
        return playerDataMap;
    }

    @Override
    public User createNewUser(UUID uuid) {
        Player player = plugin.getServer().getPlayer(uuid);
        if (player == null) {
            throw new IllegalArgumentException("Cannot create user for offline player!");
        }
        User user = new BukkitUser(player, plugin);
        // Set all skills to level 1 for new players
        for (LoadedSkill loadedSkill : plugin.getSkillManager().getSkills()) {
            user.setSkillLevel(loadedSkill.skill(), 1);
            user.setSkillXp(loadedSkill.skill(), 0.0);
        }
        return user;
    }

    @Override
    public Set<User> getOnlineUsers() {
        Set<User> online = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerDataMap.containsKey(player.getUniqueId())) {
                online.add(playerDataMap.get(player.getUniqueId()));
            }
        }
        return online;
    }
}
