package com.archyx.aureliumskills.menus.skills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.AbstractMenu;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SkillsMenu extends AbstractMenu implements MenuProvider {

    public SkillsMenu(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void onOpen(Player player, ActiveMenu menu) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        // Add item click listeners
        menu.getItem("skill", Skill.class).setClickListener((click, skill) -> {
            if (player.hasPermission("aureliumskills." + skill.toString().toLowerCase(Locale.ENGLISH))) {
                int page = getPage(skill, playerData);
                Map<String, Object> properties = new HashMap<>();
                properties.put("skill", skill);
                plugin.getSlate().getMenuManager().openMenu(player, "level_progression", properties, page);
            }
        });
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu) {
        Locale locale = plugin.getLang().getLocale(player);
        if (placeholder.equals("skills_menu_title")) {
            return Lang.getMessage(MenuMessage.SKILLS_MENU_TITLE, locale);
        }
        return placeholder;
    }

    private int getPage(Skill skill, PlayerData playerData) {
        int page = (playerData.getSkillLevel(skill) - 2) / 24;
        int maxLevelPage = (OptionL.getMaxLevel(skill) - 2) / 24;
        if (page > maxLevelPage) {
            page = maxLevelPage;
        }
        return page;
    }

}
