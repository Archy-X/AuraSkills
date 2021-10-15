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

import java.util.Locale;

public class AbilitiesMenu extends AbstractMenu implements MenuProvider {

    public AbilitiesMenu(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu) {
        Locale locale = plugin.getLang().getLocale(player);
        if ("abilities_title".equals(placeholder)) {
            Skill skill = (Skill) menu.getProperty("skill");
            return TextUtil.replace(Lang.getMessage(MenuMessage.ABILITIES_TITLE, locale),
                    "{skill}", skill.getDisplayName(locale));
        }
        return placeholder;
    }

}
