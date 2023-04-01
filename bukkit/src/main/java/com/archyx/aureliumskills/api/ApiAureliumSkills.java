package com.archyx.aureliumskills.api;

import com.archyx.aureliumskills.api.implementation.ApiMessageManager;
import com.archyx.aureliumskills.api.implementation.ApiPlayerManager;
import com.archyx.aureliumskills.api.implementation.ApiXpRequirements;
import dev.aurelium.skills.api.AureliumSkills;
import dev.aurelium.skills.api.config.AbilityConfig;
import dev.aurelium.skills.api.config.ConfigManager;
import dev.aurelium.skills.api.config.ManaAbilityConfig;
import dev.aurelium.skills.api.message.MessageManager;
import dev.aurelium.skills.api.player.PlayerManager;
import dev.aurelium.skills.api.skill.XpRequirements;

public class ApiAureliumSkills implements AureliumSkills {

    private final com.archyx.aureliumskills.AureliumSkills plugin;
    private final PlayerManager playerManager;
    private final MessageManager messageManager;
    private final XpRequirements xpRequirements;

    public ApiAureliumSkills(com.archyx.aureliumskills.AureliumSkills plugin) {
        this.plugin = plugin;
        this.playerManager = new ApiPlayerManager(plugin);
        this.messageManager = new ApiMessageManager(plugin);
        this.xpRequirements = new ApiXpRequirements(plugin);
    }

    @Override
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public ConfigManager getConfigManager() {
        return null;
    }

    @Override
    public XpRequirements getXpRequirements() {
        return xpRequirements;
    }

    @Override
    public AbilityConfig getAbilityConfig() {
        return null;
    }

    @Override
    public ManaAbilityConfig getManaAbilityConfig() {
        return null;
    }
}
