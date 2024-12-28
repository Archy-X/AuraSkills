package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.user.UserManager;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ApiUserManager implements UserManager {

    private final AuraSkillsPlugin plugin;

    public ApiUserManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public SkillsUser getUser(UUID playerId) {
        User user = plugin.getUserManager().getUser(playerId);
        if (user != null) {
            return new ApiSkillsUser(user);
        }
        return new OfflineSkillsUser(plugin, playerId);
    }

    @Override
    public CompletableFuture<SkillsUser> loadUser(UUID playerId) {
        CompletableFuture<SkillsUser> future = new CompletableFuture<>();
        User onlineUser = plugin.getUserManager().getUser(playerId);
        if (onlineUser != null) {
            future.complete(new ApiSkillsUser(onlineUser));
        } else {
            plugin.getScheduler().executeAsync(() -> {
                try {
                    plugin.getStorageProvider().load(playerId);
                    User loadedUser = plugin.getUserManager().getUser(playerId);
                    if (loadedUser != null) {
                        future.complete(new ApiSkillsUser(loadedUser));
                    } else {
                        future.complete(new OfflineSkillsUser(plugin, playerId));
                    }
                } catch (Exception e) {
                    future.complete(new OfflineSkillsUser(plugin, playerId));
                    e.printStackTrace();
                }
            });
        }
        return future;
    }

}
