package com.archyx.aureliumskills.menus.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.AbstractMenu;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class StatsMenu extends AbstractMenu implements MenuProvider {

    public StatsMenu(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void onOpen(Player player, ActiveMenu activeMenu) {

    }

    @Override
    public String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, ActiveMenu activeMenu) {
        Locale locale = plugin.getLang().getLocale(player);
        if (placeholder.equals("stats_menu_title")) {
            return Lang.getMessage(MenuMessage.STATS_MENU_TITLE, locale);
        }
        return placeholder;
    }
}
