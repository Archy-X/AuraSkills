package dev.aurelium.skills.api;

import dev.aurelium.skills.api.config.ConfigManager;
import dev.aurelium.skills.api.message.MessageManager;
import dev.aurelium.skills.api.player.PlayerManager;

public interface AureliumSkills {

    PlayerManager getPlayerManager();

    MessageManager getMessageManager();

    ConfigManager getConfigManager();

}
