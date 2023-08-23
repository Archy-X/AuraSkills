package dev.aurelium.auraskills.bukkit.menus.common;

import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.PlaceholderHelper;
import org.bukkit.entity.Player;

public abstract class AbstractItem {

    protected final AuraSkills plugin;
    private final PlaceholderHelper helper;

    public AbstractItem(AuraSkills plugin) {
        this.plugin = plugin;
        this.helper = new PlaceholderHelper(plugin);
    }

    protected String replaceMenuMessage(String placeholder, Player player, ActiveMenu activeMenu, String... replacements) {
        return helper.replaceMenuMessage(placeholder, player, activeMenu, replacements);
    }

}
