package com.archyx.aureliumskills.api;

import com.archyx.aureliumskills.api.implementation.ApiPlayerManager;
import dev.aurelium.skills.api.AureliumSkills;
import dev.aurelium.skills.api.config.ConfigManager;
import dev.aurelium.skills.api.message.MessageManager;
import dev.aurelium.skills.api.player.PlayerManager;
import dev.aurelium.skills.api.skill.XpRequirements;

public class ApiAureliumSkills implements AureliumSkills {

    private final com.archyx.aureliumskills.AureliumSkills plugin;
    private final PlayerManager playerManager;

    public ApiAureliumSkills(com.archyx.aureliumskills.AureliumSkills plugin) {
        this.plugin = plugin;
        this.playerManager = new ApiPlayerManager(plugin);
    }

    @Override
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public MessageManager getMessageManager() {
        return null;
    }

    @Override
    public ConfigManager getConfigManager() {
        return null;
    }

    @Override
    public XpRequirements getXpRequirements() {
        return null;
    }
}
