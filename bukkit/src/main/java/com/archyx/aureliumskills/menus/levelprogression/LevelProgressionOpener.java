package com.archyx.aureliumskills.menus.levelprogression;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.slate.menu.ConfigurableMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LevelProgressionOpener {

    private final AureliumSkills plugin;

    public LevelProgressionOpener(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, PlayerData playerData, Skill skill) {
        // Get items per page from options, default to 24
        int itemsPerPage = 24;
        ConfigurableMenu levelProgressionMenu = plugin.getSlate().getMenuManager().getMenu("level_progression");
        if (levelProgressionMenu != null) {
            Object itemsPerPageObj = levelProgressionMenu.getOptions().get("items_per_page");
            if (itemsPerPageObj != null) {
                itemsPerPage = (int) itemsPerPageObj;
            }
        }
        int page = getPage(skill, playerData, itemsPerPage);
        Map<String, Object> properties = new HashMap<>();
        properties.put("skill", skill);
        properties.put("items_per_page", itemsPerPage);
        properties.put("previous_menu", "skills");
        plugin.getMenuManager().openMenu(player, "level_progression", properties, page);
    }

    protected int getPage(Skill skill, PlayerData playerData, int itemsPerPage) {
        int page = (playerData.getSkillLevel(skill) - 1) / itemsPerPage;
        int maxLevelPage = (OptionL.getMaxLevel(skill) - 2) / itemsPerPage;
        if (page > maxLevelPage) {
            page = maxLevelPage;
        }
        return page;
    }

}
