package dev.aurelium.auraskills.bukkit.menus;

import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public abstract class AbstractComponent {

    protected final AuraSkills plugin;
    private final PlaceholderHelper helper;

    public AbstractComponent(AuraSkills plugin) {
        this.plugin = plugin;
        this.helper = new PlaceholderHelper(plugin);
    }

    protected String replaceMenuMessage(String placeholder, Player player, ActiveMenu activeMenu, String... replacements) {
        return helper.replaceMenuMessage(placeholder, player, activeMenu, replacements);
    }

    protected String replaceMenuMessages(String source, Player player, ActiveMenu activeMenu, String... replacements) {
        return helper.replaceMenuMessages(source, player, activeMenu, replacements);
    }

}
