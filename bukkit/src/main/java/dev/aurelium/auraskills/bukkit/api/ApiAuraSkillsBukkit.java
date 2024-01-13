package dev.aurelium.auraskills.bukkit.api;

import dev.aurelium.auraskills.api.AuraSkillsBukkit;
import dev.aurelium.auraskills.api.item.ItemManager;
import dev.aurelium.auraskills.api.region.Regions;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.api.implementation.ApiRegions;

public class ApiAuraSkillsBukkit implements AuraSkillsBukkit {

    private final ApiRegions blockTracker;
    private final ItemManager itemManager;

    public ApiAuraSkillsBukkit(AuraSkills plugin) {
        this.blockTracker = new ApiRegions(plugin);
        this.itemManager = plugin.getItemManager();
    }

    @Override
    public Regions getRegions() {
        return blockTracker;
    }

    @Override
    public ItemManager getItemManager() {
        return itemManager;
    }
}
