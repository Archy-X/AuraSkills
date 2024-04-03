package dev.aurelium.auraskills.bukkit.menus;

import com.archyx.slate.Slate;
import com.archyx.slate.context.ContextManager;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.contexts.*;
import dev.aurelium.auraskills.bukkit.menus.util.PlaceholderHelper;
import dev.aurelium.auraskills.common.util.text.Replacer;

public class MenuRegistrar {

    private final AuraSkills plugin;
    private final Slate slate;
    private final PlaceholderHelper placeholderHelper;

    public MenuRegistrar(AuraSkills plugin) {
        this.plugin = plugin;
        this.slate = plugin.getSlate();
        this.placeholderHelper = new PlaceholderHelper(plugin);
    }

    public void register() {
        ContextManager cm = slate.getContextManager();
        // Register contexts
        cm.registerContext("Skill", new SkillContext(plugin));
        cm.registerContext("Stat", new StatContext(plugin));
        cm.registerContext("Source", new XpSourceContext(plugin));
        cm.registerContext("SortType", new SortTypeContext());
        cm.registerContext("Ability", new AbilityContext(plugin));
        cm.registerContext("ManaAbility", new ManaAbilityContext(plugin));

        // Build menus
        buildMenus();
    }

    private void buildMenus() {
        slate.setGlobalOptions(options -> {
            options.replacer(c -> {
                // Returns null if not a menu message
                return placeholderHelper.replaceMenuMessage(c.placeholder(), null, c.player(), c.menu(), new Replacer());
            });
            options.localeProvider(plugin::getLocale);
        });

        slate.buildMenu("skills", menu -> new SkillsMenu(plugin).build(menu));
        slate.buildMenu("stats", menu -> new StatsMenu(plugin).build(menu));
        slate.buildMenu("level_progression", menu -> new LevelProgressionMenu(plugin).build(menu));
        slate.buildMenu("leaderboard", menu -> new LeaderboardMenu(plugin).build(menu));
        slate.buildMenu("sources", menu -> new SourcesMenu(plugin).build(menu));
        slate.buildMenu("abilities", menu -> new AbilitiesMenu(plugin).build(menu));
    }
}
