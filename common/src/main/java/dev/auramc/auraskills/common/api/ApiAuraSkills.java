package dev.auramc.auraskills.common.api;

import dev.auramc.auraskills.api.AuraSkillsApi;
import dev.auramc.auraskills.api.config.AbilityConfig;
import dev.auramc.auraskills.api.config.ConfigManager;
import dev.auramc.auraskills.api.config.ManaAbilityConfig;
import dev.auramc.auraskills.api.event.EventManager;
import dev.auramc.auraskills.api.message.MessageManager;
import dev.auramc.auraskills.api.player.PlayerManager;
import dev.auramc.auraskills.api.skill.XpRequirements;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.api.implementation.*;

public class ApiAuraSkills implements AuraSkillsApi {

    private final PlayerManager playerManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final XpRequirements xpRequirements;
    private final AbilityConfig abilityConfig;
    private final ManaAbilityConfig manaAbilityConfig;
    private final EventManager eventManager;

    public ApiAuraSkills(AuraSkillsPlugin plugin) {
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
