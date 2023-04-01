package dev.aurelium.skills.api;

import dev.aurelium.skills.api.config.AbilityConfig;
import dev.aurelium.skills.api.config.ConfigManager;
import dev.aurelium.skills.api.config.ManaAbilityConfig;
import dev.aurelium.skills.api.message.MessageManager;
import dev.aurelium.skills.api.player.PlayerManager;
import dev.aurelium.skills.api.skill.XpRequirements;

public interface AureliumSkills {

    PlayerManager getPlayerManager();

    MessageManager getMessageManager();

    ConfigManager getConfigManager();

    XpRequirements getXpRequirements();

    AbilityConfig getAbilityConfig();

    ManaAbilityConfig getManaAbilityConfig();

}
