package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.menu.ConfigurableMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.player.User;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LevelProgressionOpener {

    private final AuraSkills plugin;

    public LevelProgressionOpener(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, User user, Skill skill) {
        int page = getPage(skill, user);
        Map<String, Object> properties = new HashMap<>();
        properties.put("skill", skill);
        // Get items per page from options, default to 24
        int itemsPerPage = 24;
        ConfigurableMenu levelProgressionMenu = plugin.getSlate().getMenuManager().getMenu("level_progression");
        if (levelProgressionMenu != null) {
            Object itemsPerPageObj = levelProgressionMenu.getOptions().get("items_per_page");
            if (itemsPerPageObj != null) {
                itemsPerPage = (int) itemsPerPageObj;
            }
        }
        properties.put("items_per_page", itemsPerPage);
        properties.put("previous_menu", "skills");
        plugin.getMenuManager().openMenu(player, "level_progression", properties, page);
    }

    protected int getPage(Skill skill, User playerData) {
        int page = (playerData.getSkillLevel(skill) - 1) / 24;
        int maxLevelPage = (skill.getMaxLevel() - 2) / 24;
        if (page > maxLevelPage) {
            page = maxLevelPage;
        }
        return page;
    }

}
