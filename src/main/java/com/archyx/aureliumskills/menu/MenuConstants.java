package com.archyx.aureliumskills.menu;

import java.util.HashMap;
import java.util.Map;

public class MenuConstants {

    public static String[] MENU_NAMES = new String[] {"skills_menu", "stats_menu", "level_progression_menu"};
    public static Map<String, String[]> menuItems;
    public static Map<String, String[]> menuTemplates;

    static {
        menuItems = new HashMap<>();
        menuItems.put("skills_menu", new String[]{"your_skills", "close"});
        menuItems.put("stats_menu", new String[]{"skull"});
        menuItems.put("level_progression_menu", new String[]{"rank", "back", "close", "next_page", "previous_page", "skill"});
        menuTemplates = new HashMap<>();
        menuTemplates.put("skills_menu", new String[]{"skill"});
        menuTemplates.put("stats_menu", new String[]{"stat"});
        menuTemplates.put("level_progression_menu", new String[] {"unlocked", "in_progress", "locked"});
    }

}
