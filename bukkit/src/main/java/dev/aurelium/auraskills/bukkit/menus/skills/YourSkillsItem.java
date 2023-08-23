package dev.aurelium.auraskills.bukkit.menus.skills;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import org.bukkit.entity.Player;

public class YourSkillsItem extends AbstractItem implements SingleItemProvider {

    public YourSkillsItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data) {
        if (placeholder.equals("player")) {
            return player.getName();
        }
        return replaceMenuMessage(placeholder, player, activeMenu);
    }
}
