package dev.aurelium.skills.common.api.implementation;

import dev.aurelium.skills.api.player.PlayerManager;
import dev.aurelium.skills.api.player.SkillsPlayer;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.data.PlayerData;

import java.util.UUID;

public class ApiPlayerManager implements PlayerManager {

    private final AureliumSkillsPlugin plugin;

    public ApiPlayerManager(AureliumSkillsPlugin plugin) {
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
