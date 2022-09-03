package com.archyx.aureliumskills.menus.abilities;

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

public class AbilitiesMenu extends AbstractMenu implements MenuProvider {

    public AbilitiesMenu(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu menu) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "abilities_title":
                @Nullable Object property = menu.getProperty("skill");
                assert (null != property);
                Skill skill = (Skill) property;
                m = Lang.getMessage(MenuMessage.ABILITIES_TITLE, locale);
                assert (null != m);
                m = TextUtil.replace(m,
                        "{skill}", skill.getDisplayName(locale));
                break;
        }
        return m;
    }

}
