package dev.aurelium.auraskills.bukkit.menus.stats;

import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractMenu;
import org.bukkit.entity.Player;

public class StatsMenu extends AbstractMenu implements MenuProvider {

    public StatsMenu(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu) {
        return replaceMenuMessage(placeholder, player, activeMenu);
    }
}
