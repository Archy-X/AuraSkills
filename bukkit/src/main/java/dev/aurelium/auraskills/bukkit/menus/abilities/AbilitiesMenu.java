package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractMenu;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

public class AbilitiesMenu extends AbstractMenu implements MenuProvider {

    public AbilitiesMenu(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu) {
        Locale locale = plugin.getUser(player).getLocale();
        if ("abilities_title".equals(placeholder)) {
            Skill skill = (Skill) menu.getProperty("skill");
            return TextUtil.replace(plugin.getMsg(MenuMessage.ABILITIES_TITLE, locale),
                    "{skill}", skill.getDisplayName(locale));
        }
        return placeholder;
    }

}
