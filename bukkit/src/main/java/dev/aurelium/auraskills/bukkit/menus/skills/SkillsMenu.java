package dev.aurelium.auraskills.bukkit.menus.skills;

import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractMenu;
import org.bukkit.entity.Player;

public class SkillsMenu extends AbstractMenu implements MenuProvider {

    public SkillsMenu(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public void onOpen(Player player, ActiveMenu menu) {

    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu) {
        return replaceMenuMessage(placeholder, player, menu);
    }

}