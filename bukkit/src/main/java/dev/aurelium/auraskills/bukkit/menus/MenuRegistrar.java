package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.api.implementation.ApiMenuManager;
import dev.aurelium.auraskills.bukkit.menus.contexts.*;
import dev.aurelium.auraskills.bukkit.menus.util.PlaceholderHelper;
import dev.aurelium.auraskills.common.util.text.Replacer;
import dev.aurelium.slate.Slate;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.context.ContextManager;

import java.util.function.Consumer;

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
        slate.setGlobalBehavior(behavior -> {
            behavior.replacer(c -> {
                // Returns null if not a menu message
                return placeholderHelper.replaceMenuMessage(c.placeholder(), null, c.player(), c.menu(), new Replacer());
            });
            behavior.localeProvider(plugin::getLocale);
        });

        buildMenu("skills", menu -> new SkillsMenu(plugin).build(menu));
        buildMenu("stats", menu -> new StatsMenu(plugin).build(menu));
        buildMenu("level_progression", menu -> new LevelProgressionMenu(plugin).build(menu));
        buildMenu("leaderboard", menu -> new LeaderboardMenu(plugin).build(menu));
        buildMenu("sources", menu -> new SourcesMenu(plugin).build(menu));
        buildMenu("abilities", menu -> new AbilitiesMenu(plugin).build(menu));

        for (String nonDefault : ((ApiMenuManager) plugin.getApiBukkit().getMenuManager()).getNonDefaultMenuNames()) {
            buildMenu(nonDefault, menu -> {}); // Empty consumer passed since custom builder is applied in buildMenu
        }
    }

    private void buildMenu(String name, Consumer<MenuBuilder> builder) {
        slate.buildMenu(name, menu -> {
            builder.accept(menu);
            // Apply external menu builders
            ((ApiMenuManager) plugin.getApiBukkit().getMenuManager()).applyBuilders(name, menu);
        });
    }

}
