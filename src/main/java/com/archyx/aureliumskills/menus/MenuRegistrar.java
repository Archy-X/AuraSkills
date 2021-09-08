package com.archyx.aureliumskills.menus;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.menus.common.CloseItem;
import com.archyx.aureliumskills.menus.common.SkillItem;
import com.archyx.aureliumskills.menus.contexts.SkillContext;
import com.archyx.aureliumskills.menus.contexts.StatContext;
import com.archyx.aureliumskills.menus.levelprogression.*;
import com.archyx.aureliumskills.menus.skills.SkillsMenu;
import com.archyx.aureliumskills.menus.skills.YourSkillsItem;
import com.archyx.aureliumskills.menus.stats.SkullItem;
import com.archyx.aureliumskills.menus.stats.StatItem;
import com.archyx.aureliumskills.menus.stats.StatsMenu;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.slate.Slate;
import com.archyx.slate.context.ContextManager;
import com.archyx.slate.menu.MenuManager;

public class MenuRegistrar {

    private final AureliumSkills plugin;
    private final Slate slate;

    public MenuRegistrar(AureliumSkills plugin) {
        this.plugin = plugin;
        this.slate = plugin.getSlate();
    }

    public void register() {
        ContextManager contextManager = slate.getContextManager();
        // Register contexts
        contextManager.registerContext(Skill.class, new SkillContext(plugin));
        contextManager.registerContext(Stat.class, new StatContext(plugin));

        MenuManager menuManager = slate.getMenuManager();
        // Register menus
        menuManager.registerMenuProvider("skills", new SkillsMenu(plugin));
        menuManager.registerMenuProvider("stats", new StatsMenu(plugin));
        menuManager.registerMenuProvider("level_progression", new LevelProgressionMenu(plugin));
        // Register items
        menuManager.registerItemProvider("your_skills", new YourSkillsItem(plugin));
        menuManager.registerItemProvider("skill", new SkillItem(plugin));
        menuManager.registerItemProvider("close", new CloseItem(plugin));
        menuManager.registerItemProvider("skull", new SkullItem(plugin));
        menuManager.registerItemProvider("stat", new StatItem(plugin));
        menuManager.registerItemProvider("back", new BackItem(plugin));
        menuManager.registerItemProvider("next_page", new NextPageItem(plugin));
        menuManager.registerItemProvider("previous_page", new PreviousPageItem(plugin));
        menuManager.registerItemProvider("rank", new RankItem(plugin));
        menuManager.registerItemProvider("unlocked", new UnlockedItem(plugin));
        menuManager.registerItemProvider("in_progress", new InProgressItem(plugin));
        menuManager.registerItemProvider("locked", new LockedItem(plugin));
    }
}
