package dev.aurelium.auraskills.api;

import dev.aurelium.auraskills.api.event.EventManager;
import dev.aurelium.auraskills.api.message.MessageManager;
import dev.aurelium.auraskills.api.player.PlayerManager;
import dev.aurelium.auraskills.api.skill.XpRequirements;

public interface AuraSkillsApi {

    PlayerManager getPlayerManager();

    MessageManager getMessageManager();

    XpRequirements getXpRequirements();

    EventManager getEventManager();

}
