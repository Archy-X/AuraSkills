package dev.aurelium.auraskills.bukkit.menus;

import com.archyx.slate.Slate;
import com.archyx.slate.menu.ConfigurableMenu;
import dev.aurelium.auraskills.common.menu.MenuHelper;

public class SlateMenuHelper implements MenuHelper {

    private final Slate slate;

    public SlateMenuHelper(Slate slate) {
        this.slate = slate;
    }

    @Override
    public String getFormat(String menuName, String formatName) {
        ConfigurableMenu menu = slate.getMenuManager().getMenu(menuName);
        if (menu != null) {
            return menu.getFormats().getOrDefault(formatName, formatName);
        } else {
            return formatName;
        }
    }
}
