package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.menu.items.ItemType;
import com.archyx.aureliumskills.menu.templates.TemplateType;

public enum MenuType {

    SKILLS("skills_menu", new ItemType[] {ItemType.YOUR_SKILLS, ItemType.CLOSE}, new TemplateType[] {TemplateType.SKILL}),
    LEVEL_PROGRESSION("level_progression_menu", new ItemType[] {ItemType.RANK, ItemType.BACK, ItemType.CLOSE, ItemType.NEXT_PAGE, ItemType.PREVIOUS_PAGE, ItemType.SKILL},
            new TemplateType[] {TemplateType.UNLOCKED, TemplateType.IN_PROGRESS, TemplateType.LOCKED}),
    STATS("stats_menu", new ItemType[] {ItemType.SKULL}, new TemplateType[] {TemplateType.STAT});

    private final String path;
    private final ItemType[] items;
    private final TemplateType[] templates;

    MenuType(String path, ItemType[] items, TemplateType[] templates) {
        this.path = path;
        this.items = items;
        this.templates = templates;
    }

    public String getPath() {
        return path;
    }

    public ItemType[] getItems() {
        return items;
    }

    public TemplateType[] getTemplates() {
        return templates;
    }

}
