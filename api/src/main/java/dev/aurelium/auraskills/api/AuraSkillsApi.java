package dev.aurelium.auraskills.api;

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
     * @return the XP requirements
     */
    XpRequirements getXpRequirements();

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
