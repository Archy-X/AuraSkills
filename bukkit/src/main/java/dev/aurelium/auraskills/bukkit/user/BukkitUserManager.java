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
        @Nullable Player player = plugin.getServer().getPlayer(uuid);
        User user = new BukkitUser(uuid, player, plugin);
        // Set all skills to level 1 for new players
        for (LoadedSkill loadedSkill : plugin.getSkillManager().getSkills()) {
            user.setSkillLevel(loadedSkill.skill(), plugin.config().getStartLevel());
            user.setSkillXp(loadedSkill.skill(), 0.0);
        }
        return user;
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
