package dev.aurelium.auraskills.api;

import dev.aurelium.auraskills.api.item.ItemManager;
import dev.aurelium.auraskills.api.menu.MenuManager;
import dev.aurelium.auraskills.api.region.LocationManager;
import dev.aurelium.auraskills.api.region.Regions;

/**
 * The main interface for API classes that depend on the Bukkit API.
 */
public interface AuraSkillsBukkit {

    /**
     * Gets the region manager for checking and adding placed blocks.
     *
     * @return the region manager
     */
    Regions getRegions();

    /**
     * Gets the item manager used to add modifiers, multipliers, and requirements to items.
     *
     * @return the item manager
     */
    ItemManager getItemManager();

    /**
     * Gets the location manager for checking the validity of worlds and locations.
     *
     * @return the location manager
     */
    LocationManager getLocationManager();

    /**
     * Gets the menu manager for creating and extending menus.
     *
     * @return the menu manager
     */
    MenuManager getMenuManager();

    /**
     * Gets the instance of the {@link AuraSkillsBukkit} API,
     * throwing {@link IllegalStateException} if the API is not loaded yet.
     *
     * @return the Bukkit API instance
     * @throws IllegalStateException if the API is not loaded
     */
    static AuraSkillsBukkit get() {
        return AuraSkillsBukkitProvider.getInstance();
    }

}
