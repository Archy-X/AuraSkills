package dev.aurelium.auraskills.api;

import dev.aurelium.auraskills.api.event.EventManager;
import dev.aurelium.auraskills.api.message.MessageManager;
import dev.aurelium.auraskills.api.user.UserManager;
import dev.aurelium.auraskills.api.skill.XpRequirements;

public interface AuraSkillsApi {

    UserManager getUserManager();

    MessageManager getMessageManager();

    XpRequirements getXpRequirements();

    EventManager getEventManager();

}
