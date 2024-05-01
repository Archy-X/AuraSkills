package dev.aurelium.auraskills.bukkit.menus.shared;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.util.LevelProgressionOpener;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.builder.ItemBuilder;
import dev.aurelium.slate.info.ItemInfo;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GlobalItems {

    private final AuraSkills plugin;

    public GlobalItems(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void back(ItemBuilder item) {
        item.replace("menu_name", p -> TextUtil.capitalizeWord(TextUtil.replace((String) p.menu().getProperty("previous_menu"), "_", " ")));
        item.onClick(c -> plugin.getSlate().openMenu(c.player(), (String) c.menu().getProperty("previous_menu")));
        item.modify(i -> i.menu().getProperty("previous_menu") == null ? null : i.item());
    }

    public void backToLevelProgression(ItemBuilder item) {
        item.replace("menu_name", p -> TextUtil.capitalizeWord(TextUtil.replace((String) p.menu().getProperty("previous_menu"), "_", " ")));
        item.onClick(c -> new LevelProgressionOpener(plugin).open(c.player(), (Skill) c.menu().getProperty("skill")));
        item.modify(i -> i.menu().getProperty("previous_menu") == null ? null : i.item());
    }

    public void previousPage(ItemBuilder item) {
        item.onClick(c -> {
            ActiveMenu activeMenu = c.menu();
            plugin.getSlate().openMenu(c.player(), activeMenu.getName(), activeMenu.getProperties(), activeMenu.getCurrentPage() - 1);
        });
        item.modify(i -> i.menu().getCurrentPage() == 0 ? null : i.item());
    }

    public void nextPage(ItemBuilder item) {
        item.onClick(c -> {
            ActiveMenu activeMenu = c.menu();
            plugin.getSlate().openMenu(c.player(), activeMenu.getName(), activeMenu.getProperties(), activeMenu.getCurrentPage() + 1);
        });
        item.modify(i -> i.menu().getCurrentPage() == (i.menu().getTotalPages() - 1) ? null : i.item());
    }

    public void close(ItemBuilder item) {
        item.onClick(c -> c.player().closeInventory());
    }

    public ItemStack fill(ItemInfo info) {
        ItemStack item = info.item();
        ItemMeta meta = item.getItemMeta();

        if (meta != null && VersionUtils.isAtLeastVersion(20, 5)) {
            meta.setHideTooltip(true);
            item.setItemMeta(meta);
        }
        return item;
    }

}
