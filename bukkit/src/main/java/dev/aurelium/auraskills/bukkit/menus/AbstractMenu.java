package dev.aurelium.auraskills.bukkit.menus;

import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.util.text.Replacer;
import org.bukkit.entity.Player;

public abstract class AbstractMenu {

    protected final AuraSkills plugin;
    private final PlaceholderHelper helper;

    public AbstractMenu(AuraSkills plugin) {
        this.plugin = plugin;
        this.helper = new PlaceholderHelper(plugin);
    }

    protected String replaceMenuMessage(String placeholder, Player player, ActiveMenu activeMenu) {
        return helper.replaceMenuMessage(placeholder, player, activeMenu, new Replacer());
    }

    protected String replaceMenuMessage(String placeholder, Player player, ActiveMenu activeMenu, Replacer replacer) {
        return helper.replaceMenuMessage(placeholder, player, activeMenu, replacer);
    }

}
