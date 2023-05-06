package dev.auramc.auraskills.api;

import dev.auramc.auraskills.api.config.AbilityConfig;
import dev.auramc.auraskills.api.config.ConfigManager;
import dev.auramc.auraskills.api.config.ManaAbilityConfig;
import dev.auramc.auraskills.api.event.EventManager;
import dev.auramc.auraskills.api.message.MessageManager;
import dev.auramc.auraskills.api.player.PlayerManager;
import dev.auramc.auraskills.api.skill.XpRequirements;

public interface AuraSkillsApi {

    PlayerManager getPlayerManager();

    MessageManager getMessageManager();

    ConfigManager getConfigManager();

    XpRequirements getXpRequirements();

    AbilityConfig getAbilityConfig();

    ManaAbilityConfig getManaAbilityConfig();

    EventManager getEventManager();

}
