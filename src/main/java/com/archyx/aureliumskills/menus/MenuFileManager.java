package com.archyx.aureliumskills.menus;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.slate.menu.MenuManager;

import java.io.File;

public class MenuFileManager {

    private final AureliumSkills plugin;
    private final MenuManager manager;

    public MenuFileManager(AureliumSkills plugin) {
        this.plugin = plugin;
        this.manager = plugin.getMenuManager();
    }

    public void generateDefaultFiles() {
        for (String menuName : manager.getMenuProviderNames()) {
            File file = new File(plugin.getDataFolder() + "/menus", menuName + ".yml");
            if (!file.exists()) {
                plugin.saveResource("menus/" + menuName + ".yml", false);
            }
        }
    }

    public void loadMenus() {
        int menusLoaded = 0;
        for (String menuName : manager.getMenuProviderNames()) {
            File file = new File(plugin.getDataFolder() + "/menus", menuName + ".yml");
            if (file.exists()) {
                try {
                    manager.loadMenu(file);
                    menusLoaded++;
                } catch (Exception e) {
                    plugin.getLogger().warning("Error loading menu " + menuName);
                    e.printStackTrace();
                }
            }
        }
        plugin.getLogger().info("Loaded " + menusLoaded + " menus");
    }

    public void migrateLegacyFiles() {
        // TODO Migrate existing files
    }

}
