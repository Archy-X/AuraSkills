package com.archyx.aureliumskills.menus;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.menus.common.BackItem;
import com.archyx.aureliumskills.menus.common.CloseItem;
import com.archyx.aureliumskills.menus.common.NextPageItem;
import com.archyx.aureliumskills.menus.common.PreviousPageItem;
import com.archyx.aureliumskills.menus.contexts.SkillContext;
import com.archyx.aureliumskills.menus.contexts.SourceContext;
import com.archyx.aureliumskills.menus.contexts.StatContext;
import com.archyx.aureliumskills.menus.leaderboard.BackToLevelProgressionItem;
import com.archyx.aureliumskills.menus.leaderboard.LeaderboardMenu;
import com.archyx.aureliumskills.menus.leaderboard.LeaderboardPlayerItem;
import com.archyx.aureliumskills.menus.levelprogression.*;
import com.archyx.aureliumskills.menus.skills.ClickableSkillItem;
import com.archyx.aureliumskills.menus.skills.SkillsMenu;
import com.archyx.aureliumskills.menus.skills.StatsItem;
import com.archyx.aureliumskills.menus.skills.YourSkillsItem;
import com.archyx.aureliumskills.menus.sources.SorterItem;
import com.archyx.aureliumskills.menus.sources.SourceItem;
import com.archyx.aureliumskills.menus.sources.SourcesMenu;
import com.archyx.aureliumskills.menus.stats.SkullItem;
import com.archyx.aureliumskills.menus.stats.StatItem;
import com.archyx.aureliumskills.menus.stats.StatsMenu;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.slate.Slate;
import com.archyx.slate.context.ContextManager;
import com.archyx.slate.item.provider.ProviderManager;
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
        contextManager.registerContext(Source.class, new SourceContext(plugin));

        MenuManager manager = plugin.getMenuManager();
        // Register menus
        manager.registerMenuProvider("skills", new SkillsMenu(plugin));
        manager.registerMenuProvider("stats", new StatsMenu(plugin));
        manager.registerMenuProvider("level_progression", new LevelProgressionMenu(plugin));
        manager.registerMenuProvider("leaderboard", new LeaderboardMenu(plugin));
        manager.registerMenuProvider("sources", new SourcesMenu(plugin));
        // Global items
        manager.registerSingleItem("back", new BackItem(plugin));
        manager.registerSingleItem("close", new CloseItem(plugin));
        manager.registerSingleItem("next_page", new NextPageItem(plugin));
        manager.registerSingleItem("previous_page", new PreviousPageItem(plugin));
        // Register items
        ProviderManager skills = manager.getProviderManager("skills");
        skills.registerSingleItem("your_skills", new YourSkillsItem(plugin));
        skills.registerSingleItem("stats", new StatsItem(plugin));
        skills.registerTemplateItem("skill", new ClickableSkillItem(plugin));

        ProviderManager stats = manager.getProviderManager("stats");
        stats.registerSingleItem("skull", new SkullItem(plugin));
        stats.registerTemplateItem("stat", new StatItem(plugin));

        ProviderManager levelProgression = manager.getProviderManager("level_progression");
        levelProgression.registerSingleItem("next_page", new NextPageItem(plugin));
        levelProgression.registerSingleItem("previous_page", new PreviousPageItem(plugin));
        levelProgression.registerSingleItem("rank", new RankItem(plugin));
        levelProgression.registerSingleItem("sources", new SourcesItem(plugin));
        levelProgression.registerTemplateItem("skill", new StaticSkillItem(plugin));
        levelProgression.registerTemplateItem("unlocked", new UnlockedItem(plugin));
        levelProgression.registerTemplateItem("in_progress", new InProgressItem(plugin));
        levelProgression.registerTemplateItem("locked", new LockedItem(plugin));

        ProviderManager leaderboard = manager.getProviderManager("leaderboard");
        leaderboard.registerSingleItem("back", new BackToLevelProgressionItem(plugin));
        leaderboard.registerTemplateItem("leaderboard_player", new LeaderboardPlayerItem(plugin));

        ProviderManager sources = manager.getProviderManager("sources");
        sources.registerSingleItem("sorter", new SorterItem(plugin));
        sources.registerTemplateItem("source", new SourceItem(plugin));
    }
}
