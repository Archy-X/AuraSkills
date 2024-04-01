package dev.aurelium.auraskills.bukkit.menus;

import com.archyx.slate.Slate;
import com.archyx.slate.context.ContextManager;
import com.archyx.slate.item.provider.ProviderManager;
import com.archyx.slate.menu.MenuManager;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.ItemRegistryMenuProvider;
import dev.aurelium.auraskills.bukkit.menus.common.BackItem;
import dev.aurelium.auraskills.bukkit.menus.common.CloseItem;
import dev.aurelium.auraskills.bukkit.menus.common.NextPageItem;
import dev.aurelium.auraskills.bukkit.menus.common.PreviousPageItem;
import dev.aurelium.auraskills.bukkit.menus.contexts.*;
import dev.aurelium.auraskills.bukkit.menus.levelprogression.*;
import dev.aurelium.auraskills.bukkit.menus.skills.SkillComponents;
import dev.aurelium.auraskills.common.util.text.Replacer;

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
        contextManager.registerContext("Source", new SourceContext(plugin));
        contextManager.registerContext("SortType", new SortTypeContext());
        contextManager.registerContext("Ability", new AbilityContext(plugin));
        contextManager.registerContext("ManaAbility", new ManaAbilityContext(plugin));

        MenuManager manager = plugin.getMenuManager();
        // Register menus
        manager.registerMenuProvider("level_progression", new LevelProgressionMenu(plugin));
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
        // Global items
        manager.registerSingleItem("back", () -> new BackItem(plugin));
        manager.registerSingleItem("close", () -> new CloseItem(plugin));
        manager.registerSingleItem("next_page", () -> new NextPageItem(plugin));
        manager.registerSingleItem("previous_page", () -> new PreviousPageItem(plugin));
        // Keyed item provider
        manager.getGlobalProviderManager().registerKeyedItemProvider(new ItemRegistryMenuProvider(plugin.getItemRegistry()));

        // Register menu specific items and templates
        ProviderManager levelProgression = manager.getProviderManager("level_progression");
        levelProgression.registerSingleItem("next_page", () -> new NextPageItem(plugin));
        levelProgression.registerSingleItem("previous_page", () -> new PreviousPageItem(plugin));
        levelProgression.registerSingleItem("rank", () -> new RankItem(plugin));
        levelProgression.registerSingleItem("sources", () -> new SourcesItem(plugin));
        levelProgression.registerSingleItem("abilities", () -> new AbilitiesItem(plugin));
        levelProgression.registerTemplateItem("skill", Skill.class, () -> new StaticSkillItem(plugin));
        levelProgression.registerTemplateItem("unlocked", Integer.class, () -> new UnlockedItem(plugin));
        levelProgression.registerTemplateItem("in_progress", Integer.class, () -> new InProgressItem(plugin));
        levelProgression.registerTemplateItem("locked", Integer.class, () -> new LockedItem(plugin));
        levelProgression.registerComponent("ability_unlock", () -> new LevelProgressionComponents.AbilityUnlock(plugin));
        levelProgression.registerComponent("ability_level", () -> new LevelProgressionComponents.AbilityLevel(plugin));
        levelProgression.registerComponent("mana_ability_unlock", () -> new LevelProgressionComponents.ManaAbilityUnlock(plugin));
        levelProgression.registerComponent("mana_ability_level", () -> new LevelProgressionComponents.ManaAbilityLevel(plugin));
        levelProgression.registerComponent("rewards", () -> new LevelProgressionComponents.Rewards(plugin));

        // Register components for the skill item in skills and level progression menu
        for (ProviderManager providerManager : new ProviderManager[] {levelProgression}) {
            providerManager.registerComponent("stats_leveled", () -> new SkillComponents.StatsLeveled(plugin));
            providerManager.registerComponent("ability_levels", () -> new SkillComponents.AbilityLevels(plugin));
            providerManager.registerComponent("mana_ability_info", () -> new SkillComponents.ManaAbilityInfo(plugin));
            providerManager.registerComponent("progress", () -> new SkillComponents.Progress(plugin));
            providerManager.registerComponent("max_level", () -> new SkillComponents.MaxLevel(plugin));
        }
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
        slate.buildMenu("leaderboard", menu -> new LeaderboardMenu(plugin).build(menu));
        slate.buildMenu("sources", menu -> new SourcesMenu(plugin).build(menu));
        slate.buildMenu("abilities", menu -> new AbilitiesMenu(plugin).build(menu));
    }
}
