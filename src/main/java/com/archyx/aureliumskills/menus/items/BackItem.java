package com.archyx.aureliumskills.menus.items;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import java.util.Locale;

public class BackItem extends AbstractItem implements SingleItemProvider {

    public BackItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu) {
        Locale locale = plugin.getLang().getLocale(player);
        switch (placeholder) {
            case "back":
                return Lang.getMessage(MenuMessage.BACK, locale);
            case "back_click":
                return Lang.getMessage(MenuMessage.BACK_CLICK, locale);
        }
        return placeholder;
    }
}
