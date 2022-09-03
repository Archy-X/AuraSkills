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
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        Skill skill = (Skill) activeMenu.getProperty("skill");
        assert (null != skill);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "leaderboard_menu_title":
                m = TextUtil.replace(Lang.getMessage(MenuMessage.LEADERBOARD_TITLE, locale),
                    "{skill}", skill.getDisplayName(locale));
                break;
        }
        assert (null != m);
        return m;
    }

}
