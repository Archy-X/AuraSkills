package dev.aurelium.auraskills.bukkit.menus;

import com.archyx.slate.Slate;
import com.archyx.slate.context.ContextManager;
import com.archyx.slate.item.provider.ProviderManager;
import com.archyx.slate.menu.MenuManager;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.ItemRegistryMenuProvider;
import dev.aurelium.auraskills.bukkit.menus.abilities.*;
import dev.aurelium.auraskills.bukkit.menus.common.*;
import dev.aurelium.auraskills.bukkit.menus.contexts.*;
import dev.aurelium.auraskills.bukkit.menus.leaderboard.LeaderboardMenu;
import dev.aurelium.auraskills.bukkit.menus.leaderboard.LeaderboardPlayerItem;
import dev.aurelium.auraskills.bukkit.menus.levelprogression.*;
import dev.aurelium.auraskills.bukkit.menus.skills.*;
import dev.aurelium.auraskills.bukkit.menus.sources.SorterItem;
import dev.aurelium.auraskills.bukkit.menus.sources.SourceItem;
import dev.aurelium.auraskills.bukkit.menus.sources.SourcesComponents;
import dev.aurelium.auraskills.bukkit.menus.sources.SourcesMenu;
import dev.aurelium.auraskills.bukkit.menus.stats.SkullItem;
import dev.aurelium.auraskills.bukkit.menus.stats.StatItem;
import dev.aurelium.auraskills.bukkit.menus.stats.StatsComponents;
import dev.aurelium.auraskills.bukkit.menus.stats.StatsMenu;
import dev.aurelium.auraskills.common.source.Source;

import java.util.HashMap;
import java.util.Map;

public class MenuRegistrar {

    private final AuraSkills plugin;
    private final Slate slate;

    public MenuRegistrar(AuraSkills plugin) {
        this.plugin = plugin;
        this.slate = plugin.getSlate();
    }

    public void register() {
        ContextManager contextManager = slate.getContextManager();
        // Register contexts
        contextManager.registerContext("Skill", Skill.class, new SkillContext(plugin));
        contextManager.registerContext("Stat", Stat.class, new StatContext(plugin));
        contextManager.registerContext("Source", XpSource.class, new SourceContext(plugin));
        contextManager.registerContext("SortType", SorterItem.SortType.class, new SortTypeContext());
        contextManager.registerContext("Ability", Ability.class, new AbilityContext(plugin));
        contextManager.registerContext("ManaAbility", ManaAbility.class, new ManaAbilityContext(plugin));

        MenuManager manager = plugin.getMenuManager();
        // Register menus
        manager.registerMenuProvider("skills", new SkillsMenu(plugin));
        manager.registerMenuProvider("stats", new StatsMenu(plugin));
        manager.registerMenuProvider("level_progression", new LevelProgressionMenu(plugin));
        manager.registerMenuProvider("leaderboard", new LeaderboardMenu(plugin));
        manager.registerMenuProvider("sources", new SourcesMenu(plugin));
        manager.registerMenuProvider("abilities", new AbilitiesMenu(plugin));
        // Register default options
        Map<String, Object> levelProgressionOptions = new HashMap<>();
        levelProgressionOptions.put("use_level_as_amount", false);
        levelProgressionOptions.put("over_max_stack_amount", 1);
        levelProgressionOptions.put("items_per_page", 24);
        manager.registerDefaultOptions("level_progression", levelProgressionOptions);
        // Global items
        manager.registerSingleItem("back", () -> new BackItem(plugin));
        manager.registerSingleItem("close", () -> new CloseItem(plugin));
        manager.registerSingleItem("next_page", () -> new NextPageItem(plugin));
        manager.registerSingleItem("previous_page", () -> new PreviousPageItem(plugin));
        // Keyed item provider
        manager.getGlobalProviderManager().registerKeyedItemProvider(new ItemRegistryMenuProvider(plugin.getItemRegistry()));

        // Register menu specific items and templates
        ProviderManager skills = manager.getProviderManager("skills");
        skills.registerSingleItem("your_skills", () -> new YourSkillsItem(plugin));
        skills.registerSingleItem("stats", () -> new StatsItem(plugin));
        skills.registerTemplateItem("skill", Skill.class, () -> new ClickableSkillItem(plugin));

        ProviderManager stats = manager.getProviderManager("stats");
        stats.registerSingleItem("skull", () -> new SkullItem(plugin));
        stats.registerTemplateItem("stat", Stat.class, () -> new StatItem(plugin));
        stats.registerComponent("leveled_by", () -> new StatsComponents.LeveledBy(plugin));

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

        // Register components for the skill item in skills and level progression menu
        for (ProviderManager providerManager : new ProviderManager[] {skills, levelProgression}) {
            providerManager.registerComponent("stats_leveled", () -> new SkillComponents.StatsLeveled(plugin));
            providerManager.registerComponent("ability_levels", () -> new SkillComponents.AbilityLevels(plugin));
            providerManager.registerComponent("mana_ability_info", () -> new SkillComponents.ManaAbilityInfo(plugin));
            providerManager.registerComponent("progress", () -> new SkillComponents.Progress(plugin));
            providerManager.registerComponent("max_level", () -> new SkillComponents.MaxLevel(plugin));
        }

        ProviderManager leaderboard = manager.getProviderManager("leaderboard");
        leaderboard.registerSingleItem("back", () -> new BackToLevelProgressionItem(plugin));
        leaderboard.registerTemplateItem("leaderboard_player", Integer.class, () -> new LeaderboardPlayerItem(plugin));

        ProviderManager sources = manager.getProviderManager("sources");
        sources.registerSingleItem("sorter", () -> new SorterItem(plugin));
        sources.registerSingleItem("back", () -> new BackToLevelProgressionItem(plugin));
        sources.registerTemplateItem("source", Source.class, () -> new SourceItem(plugin));
        sources.registerComponent("multiplied_xp", () -> new SourcesComponents.MultipliedXp(plugin));

        ProviderManager abilities = manager.getProviderManager("abilities");
        abilities.registerSingleItem("back", () -> new BackToLevelProgressionItem(plugin));
        abilities.registerTemplateItem("locked_ability", Ability.class, () -> new LockedAbilityItem(plugin));
        abilities.registerTemplateItem("locked_mana_ability", ManaAbility.class, () -> new LockedManaAbilityItem(plugin));
        abilities.registerTemplateItem("unlocked_ability", Ability.class, () -> new UnlockedAbilityItem(plugin));
        abilities.registerTemplateItem("unlocked_mana_ability", ManaAbility.class, () -> new UnlockedManaAbilityItem(plugin));
        abilities.registerComponent("your_level", () -> new AbilitiesComponents.YourLevel(plugin));
        abilities.registerComponent("your_level_maxed", () -> new AbilitiesComponents.YourLevelMaxed(plugin));
        abilities.registerComponent("unlocked_desc", () -> new AbilitiesComponents.UnlockedDesc(plugin));
        abilities.registerComponent("unlocked_desc_maxed", () -> new AbilitiesComponents.UnlockedDescMaxed(plugin));
    }
}
