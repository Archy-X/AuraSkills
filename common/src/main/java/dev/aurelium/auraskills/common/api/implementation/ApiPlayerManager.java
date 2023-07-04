package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.player.PlayerManager;
import dev.aurelium.auraskills.api.player.SkillsPlayer;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.player.User;

import java.util.UUID;

public class ApiPlayerManager implements PlayerManager {

    private final AuraSkillsPlugin plugin;

    public ApiPlayerManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public SkillsPlayer getPlayer(UUID playerId) {
        User user = plugin.getUserManager().getUser(playerId);
        if (user != null) {
            return new ApiSkillsPlayer(user);
        }
        return null;
    }

}
