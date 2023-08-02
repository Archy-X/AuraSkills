package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.user.UserManager;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;

import java.util.UUID;

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
        return null;
    }

}
