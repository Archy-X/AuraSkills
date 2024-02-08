package dev.aurelium.auraskills.bukkit.menus;

import com.archyx.slate.menu.MenuManager;
import dev.aurelium.auraskills.bukkit.AuraSkills;

import java.io.File;

public class MenuFileManager {

    private final AuraSkills plugin;
    private final MenuManager manager;
    private final String[] MENU_NAMES = new String[]{"abilities", "leaderboard", "level_progression", "skills", "sources", "stats"};

    public MenuFileManager(AuraSkills plugin) {
        this.plugin = plugin;
        this.manager = plugin.getMenuManager();
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
        for (String menuName : manager.getMenuProviderNames()) {
            try {
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
            } catch (Exception e) {
                plugin.getLogger().severe("Error loading menu " + menuName);
                e.printStackTrace();
            }
        }
        plugin.getLogger().info("Loaded " + menusLoaded + " menus");
    }
}
