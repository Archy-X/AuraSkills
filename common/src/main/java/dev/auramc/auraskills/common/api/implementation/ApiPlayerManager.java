package dev.auramc.auraskills.common.api.implementation;

import dev.auramc.auraskills.api.player.PlayerManager;
import dev.auramc.auraskills.api.player.SkillsPlayer;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;

import java.util.UUID;

public class ApiPlayerManager implements PlayerManager {

    private final AuraSkillsPlugin plugin;

    public ApiPlayerManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public SkillsPlayer getPlayer(UUID playerId) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(playerId);
        if (playerData != null) {
            return new ApiSkillsPlayer(playerData);
        }
        return null;
    }

}
