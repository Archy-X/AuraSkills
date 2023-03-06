package com.archyx.aureliumskills.api.implementation;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import dev.aurelium.skills.api.player.PlayerManager;
import dev.aurelium.skills.api.player.SkillsPlayer;

import java.util.UUID;

public class ApiPlayerManager implements PlayerManager {

    private final AureliumSkills plugin;

    public ApiPlayerManager(AureliumSkills plugin) {
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
