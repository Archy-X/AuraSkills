package dev.aurelium.auraskills.bukkit.menus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public enum MenuFileUpdates {

    SKILLS_1("skills", 1, Map.of(
            "components", List.of("skill_job_active"))),
    LEVEL_PROGRESSION_1("level_progression", 1, Map.of(
            "items", List.of("job"),
            "components", List.of("job_select", "job_active", "job_limit"))),
    LEVEL_PROGRESSION_2("level_progression", 2, Map.of(
            "components", List.of("job_cooldown")
    ));

    private final String menu;
    private final int version;
    private final Map<String, List<String>> addedKeys;

    MenuFileUpdates(String menu, int version, Map<String, List<String>> addedKeys) {
        this.menu = menu;
        this.version = version;
        this.addedKeys = addedKeys;
    }

    public String getMenu() {
        return menu;
    }

    public int getVersion() {
        return version;
    }

    public Map<String, List<String>> getAddedKeys() {
        return addedKeys;
    }

    public static List<MenuFileUpdates> getUpdates(String menu, int currentVersion, int updatedVersion) {
        List<MenuFileUpdates> updates = new ArrayList<>();
        for (MenuFileUpdates update : MenuFileUpdates.values()) {
            if (!update.getMenu().equals(menu)) continue;

            if (update.getVersion() > currentVersion && update.getVersion() <= updatedVersion) {
                updates.add(update);
            }
        }
        // Sort by increasing version
        updates.sort(Comparator.comparingInt(MenuFileUpdates::getVersion));
        return updates;
    }

}
