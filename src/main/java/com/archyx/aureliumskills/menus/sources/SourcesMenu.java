package com.archyx.aureliumskills.menus.sources;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.AbstractMenu;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import org.bukkit.entity.Player;

import java.util.Locale;

public class SourcesMenu extends AbstractMenu implements MenuProvider {

    public SourcesMenu(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu) {
        Locale locale = plugin.getLang().getLocale(player);
        if (placeholder.equals("sources_title")) {
            Skill skill = (Skill) activeMenu.getProperty("skill");
            return TextUtil.replace(Lang.getMessage(MenuMessage.SOURCES_TITLE, locale),
                    "{skill}", skill.getDisplayName(locale),
                    "{current_page}", String.valueOf(activeMenu.getCurrentPage() + 1),
                    "{total_pages}", String.valueOf(activeMenu.getTotalPages()));
        }
        return placeholder;
    }

    @Override
    public int getPages(Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int itemsPerPage = (Integer) activeMenu.getProperty("items_per_page");
        Source[] sources = plugin.getSourceRegistry().values(skill);
        return (sources.length - 1) / itemsPerPage + 1;
    }
}
