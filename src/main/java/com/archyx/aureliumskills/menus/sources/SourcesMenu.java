package com.archyx.aureliumskills.menus.sources;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.AbstractMenu;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class SourcesMenu extends AbstractMenu implements MenuProvider {

    public SourcesMenu(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "sources_title":
            Skill skill = (Skill) activeMenu.getProperty("skill");
            assert (null != skill);
            m = TextUtil.replace(Lang.getMessage(MenuMessage.SOURCES_TITLE, locale),
                    "{skill}", skill.getDisplayName(locale),
                    "{current_page}", String.valueOf(activeMenu.getCurrentPage() + 1),
                    "{total_pages}", String.valueOf(activeMenu.getTotalPages()));
            break;
        }
        assert (null != m);
        return m;
    }

    @Override
    public int getPages(Player player, @NotNull ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        @Nullable Object property = activeMenu.getProperty("items_per_page");
        assert (null != property);
        int itemsPerPage = (Integer) property;
        Source[] sources = plugin.getSourceRegistry().values(skill);
        return (sources.length - 1) / itemsPerPage + 1;
    }
}
