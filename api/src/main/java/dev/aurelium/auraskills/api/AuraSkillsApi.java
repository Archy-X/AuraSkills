package dev.aurelium.auraskills.api;

import dev.aurelium.auraskills.api.message.MessageManager;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.XpRequirements;
import dev.aurelium.auraskills.api.user.UserManager;

import java.io.File;

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
     * Gets the {@link NamespacedRegistry} for the given namespace and content directory,
     * which is used to register custom skills, stats, abilities, etc.
     *
     * @param namespace The name of the plugin this is being called by to uniquely identify custom content.
     *                  The namespace will be forced to lowercase. Referencing custom content in config files is done
     *                  by appending "namespace/" to the name of the content.
     * @param contentDirectory The directory where configuration files for custom content will be loaded from.
     *                         For Bukkit, pass in JavaPlugin#getDataFolder to use the plugin's config folder.
     * @return the namespaced registry to register custom content
     * @throws IllegalArgumentException if the namespace is "auraskills", which is not allowed
     */
    NamespacedRegistry getRegistry(String namespace, File contentDirectory);

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
