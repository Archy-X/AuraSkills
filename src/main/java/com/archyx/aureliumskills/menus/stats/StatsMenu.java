package com.archyx.aureliumskills.menus.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.AbstractMenu;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class StatsMenu extends AbstractMenu implements MenuProvider {

    public StatsMenu(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void onOpen(@NotNull Player player, @NotNull ActiveMenu activeMenu) {

    }

    @Override
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "stats_menu_title":
                m = Lang.getMessage(MenuMessage.STATS_MENU_TITLE, locale);
                break;
        }
        assert (null != m);
        return placeholder;
    }
}
