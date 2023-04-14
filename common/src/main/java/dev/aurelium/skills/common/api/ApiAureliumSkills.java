package dev.aurelium.skills.common.api;

import dev.aurelium.skills.api.AureliumSkillsApi;
import dev.aurelium.skills.api.config.AbilityConfig;
import dev.aurelium.skills.api.config.ConfigManager;
import dev.aurelium.skills.api.config.ManaAbilityConfig;
import dev.aurelium.skills.api.event.EventManager;
import dev.aurelium.skills.api.message.MessageManager;
import dev.aurelium.skills.api.player.PlayerManager;
import dev.aurelium.skills.api.skill.XpRequirements;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.api.implementation.*;

public class ApiAureliumSkills implements AureliumSkillsApi {

    private final PlayerManager playerManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final XpRequirements xpRequirements;
    private final AbilityConfig abilityConfig;
    private final ManaAbilityConfig manaAbilityConfig;
    private final EventManager eventManager;

    public ApiAureliumSkills(AureliumSkillsPlugin plugin) {
        this.playerManager = new ApiPlayerManager(plugin);
        this.messageManager = new ApiMessageManager(plugin);
        this.xpRequirements = new ApiXpRequirements(plugin);
        this.configManager = new ApiConfigManager(plugin);
        this.abilityConfig = new ApiAbilityConfig(plugin);
        this.manaAbilityConfig = new ApiManaAbilityConfig(plugin);
        this.eventManager = plugin.getEventManager();
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
        return configManager;
    }

    @Override
    public XpRequirements getXpRequirements() {
        return xpRequirements;
    }

    @Override
    public AbilityConfig getAbilityConfig() {
        return abilityConfig;
    }

    @Override
    public ManaAbilityConfig getManaAbilityConfig() {
        return manaAbilityConfig;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

}
