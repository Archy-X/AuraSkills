package dev.aurelium.auraskills.bukkit.menus;

import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

public abstract class AbstractComponent {

    protected final AuraSkills plugin;

    public AbstractComponent(AuraSkills plugin) {
        this.plugin = plugin;
    }

    protected String replaceMenuMessage(String placeholder, Player player, ActiveMenu activeMenu, String... replacements) {
        if (!placeholder.startsWith("{") || !placeholder.endsWith("}")) {
            return placeholder;
        }
        Locale locale = plugin.getUser(player).getLocale();
        String stripped = TextUtil.replace(placeholder, "{", "", "}", ""); // Remove curly braces

        MessageKey key = MessageKey.of("menus." + activeMenu.getName().toLowerCase(Locale.ROOT) + "." + stripped);
        String message = plugin.getMsg(key, locale);
        // No message found in menu section, try common section
        if (message.equals(key.getPath())) {
            message = plugin.getMsg(MessageKey.of("menus.common." + stripped), locale);
        }
        // Replace placeholders
        message = TextUtil.replace(message, replacements);
        return message;
    }

    protected String replaceMenuMessages(String source, Player player, ActiveMenu activeMenu, String... replacements) {
        String[] placeholders = com.archyx.slate.util.TextUtil.substringsBetween(source, "{", "}");
        if (placeholders == null) return source;

        for (String placeholder : placeholders) {
            source = replaceMenuMessage(placeholder, player, activeMenu, replacements);
        }
        return source;
    }

}
