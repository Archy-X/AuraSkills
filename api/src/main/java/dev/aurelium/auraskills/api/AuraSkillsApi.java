package dev.aurelium.auraskills.api;

import dev.aurelium.auraskills.api.event.EventManager;
import dev.aurelium.auraskills.api.message.MessageManager;
import dev.aurelium.auraskills.api.skill.XpRequirements;
import dev.aurelium.auraskills.api.user.UserManager;

public interface AuraSkillsApi {

    /**
     * Gets the {@link UserManager}, which is used to access player-related data in the plugin,
     * including player skill and stat levels.
     *
     * @return the user manager
     */
    UserManager getUserManager();

    /**
     * Gets the {@link MessageManager}, which contains common user-configured messages,
     * such as skill names and descriptions.
     *
     * @return the message manager
     */
    MessageManager getMessageManager();

    /**
     * Gets information about the XP required to level up skills
     *
     * @return the xp requirements
     */
    XpRequirements getXpRequirements();

    /**
     * Gets the {@link EventManager}, which is used to register event handlers for
     * AuraSkills events. You must use this method to register event handlers since
     * events in AuraSkills are independent of the Bukkit event system.
     *
     * @return the event manager
     */
    EventManager getEventManager();

    /**
     * Gets the instance of the {@link AuraSkillsApi},
     * throwing {@link IllegalStateException} if the API is not loaded yet.
     *
     * @return the api instance
     * @throws IllegalStateException if the API is not loaded
     */
    static AuraSkillsApi get() {
        return AuraSkillsProvider.getInstance();
    }

}
