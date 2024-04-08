package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.api.ApiAuraSkills;

import java.io.File;

public class MenuFileManager {

    private final AuraSkills plugin;
    public static final String[] MENU_NAMES = {"abilities", "leaderboard", "level_progression", "skills", "sources", "stats"};

    public MenuFileManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void generateDefaultFiles() {
        for (String menuName : MENU_NAMES) {
            File file = new File(plugin.getDataFolder() + "/menus", menuName + ".yml");
            if (!file.exists()) {
                plugin.saveResource("menus/" + menuName + ".yml", false);
            }
        }
    }

    public void loadMenus() {
        // Add menu directories as merge directories in Slate
        var api = (ApiAuraSkills) plugin.getApi();
        for (NamespacedRegistry registry : api.getNamespacedRegistryMap().values()) {
            registry.getMenuDirectory().ifPresent(dir -> plugin.getSlate().addMergeDirectory(dir));
        }

        int menusLoaded = plugin.getSlate().loadMenus();
        plugin.getLogger().info("Loaded " + menusLoaded + " menus");
    }
}
