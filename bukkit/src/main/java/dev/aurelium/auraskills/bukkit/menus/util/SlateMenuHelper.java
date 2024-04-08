package dev.aurelium.auraskills.bukkit.menus.util;

import dev.aurelium.auraskills.common.menu.MenuHelper;
import dev.aurelium.slate.Slate;
import dev.aurelium.slate.menu.LoadedMenu;

public class SlateMenuHelper implements MenuHelper {

    private final Slate slate;

    public SlateMenuHelper(Slate slate) {
        this.slate = slate;
    }

    @Override
    public String getFormat(String menuName, String formatName) {
        LoadedMenu menu = slate.getLoadedMenu(menuName);
        if (menu != null) {
            return menu.formats().getOrDefault(formatName, formatName);
        } else {
            return formatName;
        }
    }
}
