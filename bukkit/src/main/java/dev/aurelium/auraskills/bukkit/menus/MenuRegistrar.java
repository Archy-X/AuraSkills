package dev.aurelium.auraskills.bukkit.menus;

import com.archyx.slate.Slate;
import com.archyx.slate.context.ContextManager;
import com.archyx.slate.menu.MenuManager;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.ItemRegistryMenuProvider;
import dev.aurelium.auraskills.bukkit.menus.contexts.*;
import dev.aurelium.auraskills.bukkit.menus.shared.SkillLevelItem;
import dev.aurelium.auraskills.bukkit.menus.util.PlaceholderHelper;
import dev.aurelium.auraskills.common.util.text.Replacer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
        ContextManager contextManager = slate.getContextManager();
        // Register contexts
        contextManager.registerContext("Skill", new SkillContext(plugin));
        contextManager.registerContext("Stat", new StatContext(plugin));
        contextManager.registerContext("Source", new XpSourceContext(plugin));
        contextManager.registerContext("SortType", new SortTypeContext());
        contextManager.registerContext("Ability", new AbilityContext(plugin));
        contextManager.registerContext("ManaAbility", new ManaAbilityContext(plugin));

        MenuManager manager = registerDefaultOptions();
        // Keyed item provider
        manager.getGlobalProviderManager().registerKeyedItemProvider(new ItemRegistryMenuProvider(plugin.getItemRegistry()));

        // Build menus
        buildMenus();
    }

    @NotNull
    private MenuManager registerDefaultOptions() {
        MenuManager manager = plugin.getMenuManager();
        // Register default options
        Map<String, Object> skillsOptions = Map.of("bar_length", 20);
        manager.registerDefaultOptions("skills", skillsOptions);
        Map<String, Object> lpOptions = Map.of(
                "use_level_as_amount", false,
                "over_max_stack_amount", 1,
                "items_per_page", 24,
                "start_level", 1,
                "track", SkillLevelItem.getDefaultTrack());
        manager.registerDefaultOptions("level_progression", lpOptions);
        Map<String, Object> sourcesOptions = Map.of(
                "source_start", SourcesMenu.DEF_SOURCE_START,
                "source_end", SourcesMenu.DEF_SOURCE_END,
                "items_per_page", SourcesMenu.DEF_ITEMS_PER_PAGE);
        manager.registerDefaultOptions("sources", sourcesOptions);
        return manager;
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
