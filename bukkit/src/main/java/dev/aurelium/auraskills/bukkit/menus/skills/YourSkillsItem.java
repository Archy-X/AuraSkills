package dev.aurelium.auraskills.bukkit.menus.skills;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

public class YourSkillsItem extends AbstractItem implements SingleItemProvider {

    public YourSkillsItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data) {
        Locale locale = plugin.getUser(player).getLocale();
        switch (placeholder) {
            case "your_skills":
                return TextUtil.replace(plugin.getMsg(MenuMessage.YOUR_SKILLS, locale),
                        "{player}", player.getName());
            case "desc":
                return plugin.getMsg(MenuMessage.YOUR_SKILLS_DESC, locale);
            case "hover":
                return plugin.getMsg(MenuMessage.YOUR_SKILLS_HOVER, locale);
            case "click":
                return plugin.getMsg(MenuMessage.YOUR_SKILLS_CLICK, locale);
        }
        return placeholder;
    }
}
