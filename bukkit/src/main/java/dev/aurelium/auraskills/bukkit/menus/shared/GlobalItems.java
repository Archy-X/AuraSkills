package dev.aurelium.auraskills.bukkit.menus.shared;

import com.archyx.slate.builder.MenuBuilder;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.levelprogression.LevelProgressionOpener;
import dev.aurelium.auraskills.common.util.text.TextUtil;

public class GlobalItems {

    private final AuraSkills plugin;

    public GlobalItems(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void back(MenuBuilder menu) {
        menu.item("back", item -> {
            item.replace("menu_name", p -> TextUtil.capitalizeWord(TextUtil.replace((String) p.menu().getProperty("previous_menu"), "_", " ")));
            item.onClick(c -> plugin.getMenuManager().openMenu(c.player(), (String) c.menu().getProperty("previous_menu")));
            item.modify(i -> i.menu().getProperty("previous_menu") == null ? null : i.item());
        });
    }

    public void backToLevelProgression(MenuBuilder menu) {
        menu.item("back", item -> {
            item.replace("menu_name", p -> TextUtil.capitalizeWord(TextUtil.replace((String) p.menu().getProperty("previous_menu"), "_", " ")));
            item.onClick(c -> new LevelProgressionOpener(plugin).open(c.player(), (Skill) c.menu().getProperty("skill")));
            item.modify(i -> i.menu().getProperty("previous_menu") == null ? null : i.item());
        });
    }

    public void previousPage(MenuBuilder menu) {
        menu.item("previous_page", item -> {
            item.onClick(c -> {
                ActiveMenu activeMenu = c.menu();
                plugin.getMenuManager().openMenu(c.player(), activeMenu.getName(), activeMenu.getProperties(), activeMenu.getCurrentPage() - 1);
            });
            item.modify(i -> i.menu().getCurrentPage() == 0 ? null : i.item());
        });
    }

    public void nextPage(MenuBuilder menu) {
        menu.item("next_page", item -> {
            item.onClick(c -> {
                ActiveMenu activeMenu = c.menu();
                plugin.getMenuManager().openMenu(c.player(), activeMenu.getName(), activeMenu.getProperties(), activeMenu.getCurrentPage() + 1);
            });
            item.modify(i -> i.menu().getCurrentPage() == (i.menu().getTotalPages() - 1) ? null : i.item());
        });
    }

    public void close(MenuBuilder menu) {
        menu.item("close", item -> item.onClick(c -> c.player().closeInventory()));
    }

}
