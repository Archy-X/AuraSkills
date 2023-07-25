package dev.aurelium.auraskills.bukkit.menus.skills;

import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractMenu;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import org.bukkit.entity.Player;

import java.util.Locale;

public class SkillsMenu extends AbstractMenu implements MenuProvider {

    public SkillsMenu(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public void onOpen(Player player, ActiveMenu menu) {

    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu) {
        Locale locale = plugin.getUser(player).getLocale();
        if (placeholder.equals("skills_menu_title")) {
            return plugin.getMsg(MenuMessage.SKILLS_TITLE, locale);
        }
        return placeholder;
    }

}
