package dev.aurelium.auraskills.bukkit.menus.stats;

import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractMenu;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import org.bukkit.entity.Player;

import java.util.Locale;

public class StatsMenu extends AbstractMenu implements MenuProvider {

    public StatsMenu(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public void onOpen(Player player, ActiveMenu activeMenu) {

    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu) {
        Locale locale = plugin.getUser(player).getLocale();
        if (placeholder.equals("stats_menu_title")) {
            return plugin.getMsg(MenuMessage.STATS_TITLE, locale);
        }
        return placeholder;
    }
}
