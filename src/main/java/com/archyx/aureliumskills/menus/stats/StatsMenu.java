package com.archyx.aureliumskills.menus.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.menus.AbstractMenu;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import org.bukkit.entity.Player;

public class StatsMenu extends AbstractMenu implements MenuProvider {

    public StatsMenu(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void onOpen(Player player, ActiveMenu activeMenu) {

    }

    @Override
    public String onPlaceholderReplace(String s, Player player, ActiveMenu activeMenu) {
        return s;
    }
}
