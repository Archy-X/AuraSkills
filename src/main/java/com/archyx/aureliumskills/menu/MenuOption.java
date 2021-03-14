package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.menu.items.ConfigurableItem;
import com.archyx.aureliumskills.menu.items.ItemType;
import com.archyx.aureliumskills.menu.templates.ConfigurableTemplate;
import com.archyx.aureliumskills.menu.templates.TemplateType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MenuOption {

    private final MenuType type;
    private String title;
    private int rows;
    private boolean fillEnabled;
    private ItemStack fillItem;
    private final Map<ItemType, ConfigurableItem> items;
    private final Map<TemplateType, ConfigurableTemplate> templates;

    public MenuOption(MenuType type) {
        this.type = type;
        this.items = new HashMap<>();
        this.templates = new HashMap<>();
    }

    public MenuType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public boolean isFillEnabled() {
        return fillEnabled;
    }

    public void setFillEnabled(boolean fillEnabled) {
        this.fillEnabled = fillEnabled;
    }

    public ItemStack getFillItem() {
        return fillItem;
    }

    public void setFillItem(ItemStack fillItem) {
        this.fillItem = fillItem;
    }

    public ConfigurableItem getItem(ItemType type) {
        return items.get(type);
    }

    public void putItem(ConfigurableItem item) {
        items.put(item.getItemType(), item);
    }

    public ConfigurableTemplate getTemplate(TemplateType type) {
        return templates.get(type);
    }

    public void putTemplate(ConfigurableTemplate template) {
        templates.put(template.getTemplateType(), template);
    }

}
