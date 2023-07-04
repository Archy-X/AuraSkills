package dev.aurelium.auraskills.bukkit.menus.leaderboard;

import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractMenu;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

public class LeaderboardMenu extends AbstractMenu implements MenuProvider {

    public LeaderboardMenu(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu) {
        Locale locale = plugin.getUser(player).getLocale();
        Skill skill = (Skill) activeMenu.getProperty("skill");
        if (placeholder.equals("leaderboard_menu_title")) {
            return TextUtil.replace(plugin.getMsg(MenuMessage.LEADERBOARD_TITLE, locale),
                    "{skill}", skill.getDisplayName(locale));
        }
        return placeholder;
    }

}
