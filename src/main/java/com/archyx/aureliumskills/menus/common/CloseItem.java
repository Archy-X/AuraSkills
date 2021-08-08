package com.archyx.aureliumskills.menus.common;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

public class CloseItem extends AbstractItem implements SingleItemProvider {

    public CloseItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu) {
        if (placeholder.equals("close")) {
            return Lang.getMessage(MenuMessage.CLOSE, plugin.getLang().getLocale(player));
        }
        return placeholder;
    }
}
