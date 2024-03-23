package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.bukkit.AuraSkills;

import java.io.File;

public class MenuFileManager {

    private final AuraSkills plugin;
    private final String[] MENU_NAMES = new String[]{"abilities", "leaderboard", "level_progression", "skills", "sources", "stats"};

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
        int menusLoaded = 0;
        for (String menuName : plugin.getMenuManager().getMenuProviderNames()) {
            try {
                File file = new File(plugin.getDataFolder() + "/menus", menuName + ".yml");
                if (file.exists()) {
                    try {
                        plugin.getMenuManager().loadMenu(file);
                        menusLoaded++;
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error loading menu " + menuName);
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error loading menu " + menuName);
                e.printStackTrace();
            }
        }
        plugin.getLogger().info("Loaded " + menusLoaded + " menus");
    }
}
