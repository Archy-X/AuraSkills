package com.archyx.aureliumskills.menu;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MenuOption {

    private final String name;
    private String title;
    private int rows;
    private boolean fillEnabled;
    private ItemStack fillItem;
    private final Map<String, ItemOption> items;
    private final Map<String, ItemTemplate> templates;

    public MenuOption(String name) {
        this.name = name;
        this.items = new HashMap<>();
        this.templates = new HashMap<>();
    }

    public String getName() {
        return name;
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

    public ItemOption getItem(String name) {
        return items.get(name);
    }

    public void putItem(ItemOption option) {
        items.put(option.getName(), option);
    }

    public ItemTemplate getTemplate(String name) {
        return templates.get(name);
    }

    public void putTemplate(ItemTemplate template) {
        templates.put(template.getName(), template);
    }

}
