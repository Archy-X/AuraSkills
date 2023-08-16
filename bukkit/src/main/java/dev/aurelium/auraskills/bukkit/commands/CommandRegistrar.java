package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.PaperCommandManager;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.sources.SorterItem;
import dev.aurelium.auraskills.common.commands.ManaCommand;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CommandRegistrar {

    private final AuraSkills plugin;

    public CommandRegistrar(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public PaperCommandManager registerCommands() {
        var manager = new PaperCommandManager(plugin);
        manager.enableUnstableAPI("help");
        manager.usePerIssuerLocale(true, false);
        manager.getCommandReplacements().addReplacement("skills_alias", "skills|sk|skill");

        registerContexts(manager);
        registerCompletions(manager);
        registerBaseCommands(manager);
        return manager;
    }

    private void registerContexts(PaperCommandManager manager) {
        var contexts = manager.getCommandContexts();
        contexts.registerContext(User.class, c -> {
            String username = c.popFirstArg();
            Player player = Bukkit.getPlayerExact(username);
            if (player != null) {
                return plugin.getUser(player);
            } else {
                throw new InvalidCommandArgument(MinecraftMessageKeys.NO_PLAYER_FOUND_SERVER, "{search}", username);
            }
        });
        contexts.registerContext(Skill.class, c -> {
            String arg = c.popFirstArg();
            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(arg));
            if (skill == null || !skill.isEnabled()) {
                Locale locale = plugin.getLocale(c.getSender());
                throw new InvalidCommandArgument(plugin.getMsg(CommandMessage.UNKNOWN_SKILL, locale));
            }
            return skill;
        });
        contexts.registerContext(Stat.class, c -> {
            String arg = c.popFirstArg();
            Stat stat = plugin.getStatRegistry().getOrNull(NamespacedId.fromDefault(arg));
            if (stat == null || !stat.isEnabled()) {
                Locale locale = plugin.getLocale(c.getSender());
                throw new InvalidCommandArgument(plugin.getMsg(CommandMessage.UNKNOWN_STAT, locale));
            }
            return stat;
        });
        contexts.registerContext(UUID.class, c -> {
            String input = c.popFirstArg();
            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                throw new InvalidCommandArgument(input + "is not a valid UUID!");
            }
        });
    }

    private void registerCompletions(PaperCommandManager manager) {
        var completions = manager.getCommandCompletions();
        completions.registerAsyncCompletion("skills", c -> {
            List<String> skills = new ArrayList<>();
            for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
                skills.add(skill.name().toLowerCase(Locale.ROOT));
            }
            return skills;
        });
        completions.registerAsyncCompletion("skills_global", c -> {
            List<String> skills = new ArrayList<>();
            skills.add("global");
            for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
                skills.add(skill.name().toLowerCase(Locale.ROOT));
            }
            return skills;
        });
        completions.registerAsyncCompletion("stats", c -> {
            List<String> stats = new ArrayList<>();
            for (Stat stat : plugin.getStatManager().getStatValues()) {
                stats.add(stat.name().toLowerCase(Locale.ROOT));
            }
            return stats;
        });
        completions.registerAsyncCompletion("modifiers", c -> {
            Player player = c.getPlayer();
            User user = plugin.getUser(player);

            return user.getStatModifiers().keySet();
        });
        completions.registerAsyncCompletion("skill_top", c -> {
            List<String> values = new ArrayList<>();
            for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
                values.add(skill.name().toLowerCase(Locale.ROOT));
            }
            values.add("average");
            return values;
        });
        completions.registerAsyncCompletion("lang", c -> plugin.getMessageProvider().getLoadedLanguages().stream().map(Locale::toLanguageTag).toList());
        completions.registerAsyncCompletion("item_keys", c -> plugin.getItemRegistry().getIds().stream().map(NamespacedId::toString).toList());
        completions.registerAsyncCompletion("sort_types", c -> {
            SorterItem.SortType[] sortTypes = SorterItem.SortType.values();
            List<String> typeNames = new ArrayList<>();
            for (SorterItem.SortType sortType : sortTypes) {
                typeNames.add(sortType.toString().toLowerCase(Locale.ROOT));
            }
            return typeNames;
        });
    }

    private void registerBaseCommands(PaperCommandManager manager) {
        manager.registerCommand(new SkillsRootCommand(plugin));
        manager.registerCommand(new StatsCommand(plugin));
        manager.registerCommand(new SkillCommand(plugin));
        manager.registerCommand(new ManaCommand(plugin));
        manager.registerCommand(new ModifierCommand(plugin));
        manager.registerCommand(new ItemCommand(plugin));
        manager.registerCommand(new ArmorCommand(plugin));
        manager.registerCommand(new ProfileCommand(plugin));
        manager.registerCommand(new BackupCommand(plugin));
        manager.registerCommand(new XpCommand(plugin));
        if (plugin.configBoolean(Option.ENABLE_SKILL_COMMANDS)) {
            if (Skills.FARMING.isEnabled()) { manager.registerCommand(new SkillCommands.FarmingCommand(plugin)); }
            if (Skills.FORAGING.isEnabled()) { manager.registerCommand(new SkillCommands.ForagingCommand(plugin)); }
            if (Skills.MINING.isEnabled()) { manager.registerCommand(new SkillCommands.MiningCommand(plugin)); }
            if (Skills.FISHING.isEnabled()) { manager.registerCommand(new SkillCommands.FishingCommand(plugin)); }
            if (Skills.EXCAVATION.isEnabled()) { manager.registerCommand(new SkillCommands.ExcavationCommand(plugin)); }
            if (Skills.ARCHERY.isEnabled()) { manager.registerCommand(new SkillCommands.ArcheryCommand(plugin)); }
            if (Skills.DEFENSE.isEnabled()) { manager.registerCommand(new SkillCommands.DefenseCommand(plugin)); }
            if (Skills.FIGHTING.isEnabled()) { manager.registerCommand(new SkillCommands.FightingCommand(plugin)); }
            if (Skills.ENDURANCE.isEnabled()) { manager.registerCommand(new SkillCommands.EnduranceCommand(plugin)); }
            if (Skills.AGILITY.isEnabled()) { manager.registerCommand(new SkillCommands.AgilityCommand(plugin)); }
            if (Skills.ALCHEMY.isEnabled()) { manager.registerCommand(new SkillCommands.AlchemyCommand(plugin)); }
            if (Skills.ENCHANTING.isEnabled()) { manager.registerCommand(new SkillCommands.EnchantingCommand(plugin)); }
            if (Skills.SORCERY.isEnabled()) { manager.registerCommand(new SkillCommands.SorceryCommand(plugin)); }
            if (Skills.HEALING.isEnabled()) { manager.registerCommand(new SkillCommands.HealingCommand(plugin)); }
            if (Skills.FORGING.isEnabled()) { manager.registerCommand(new SkillCommands.ForgingCommand(plugin)); }
        }
    }

}
