package com.archyx.aureliumskills.menus.leaderboard;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.AbstractMenu;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class LeaderboardMenu extends AbstractMenu implements MenuProvider {

    public LeaderboardMenu(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @Nullable String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu) {
        Locale locale = plugin.getLang().getLocale(player);
        Object property = activeMenu.getProperty("skill");
        assert (null != property);
        Skill skill = (Skill) property;
        if (placeholder.equals("leaderboard_menu_title")) {
            return TextUtil.replace(Lang.getMessage(MenuMessage.LEADERBOARD_TITLE, locale),
                    "{skill}", skill.getDisplayName(locale));
        }
        return placeholder;
    }

}
