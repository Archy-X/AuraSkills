package dev.aurelium.auraskills.bukkit.api;

import dev.aurelium.auraskills.api.AuraSkillsBukkit;
import dev.aurelium.auraskills.api.item.ItemManager;
import dev.aurelium.auraskills.api.menu.MenuManager;
import dev.aurelium.auraskills.api.region.LocationManager;
import dev.aurelium.auraskills.api.region.Regions;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.api.implementation.ApiLocationManager;
import dev.aurelium.auraskills.bukkit.api.implementation.ApiMenuManager;
import dev.aurelium.auraskills.bukkit.api.implementation.ApiRegions;

public class ApiAuraSkillsBukkit implements AuraSkillsBukkit {

    private final ApiRegions blockTracker;
    private final ItemManager itemManager;
    private final LocationManager locationManager;
    private final MenuManager menuManager;

    public ApiAuraSkillsBukkit(AuraSkills plugin) {
        this.blockTracker = new ApiRegions(plugin);
        this.itemManager = plugin.getItemManager();
        this.locationManager = new ApiLocationManager(plugin);
        this.menuManager = new ApiMenuManager(plugin);
    }

    @Override
    public Regions getRegions() {
        return blockTracker;
    }

    @Override
    public ItemManager getItemManager() {
        return itemManager;
    }

    @Override
    public LocationManager getLocationManager() {
        return locationManager;
    }

    @Override
    public MenuManager getMenuManager() {
        return menuManager;
    }
}
