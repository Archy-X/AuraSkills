package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.PaperCommandManager;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.CustomStat;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.CustomTrait;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.SourcesMenu.SortType;
import dev.aurelium.auraskills.common.commands.ManaCommand;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

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

        registerConditions(manager);
        registerContexts(manager);
        registerCompletions(manager);
        registerBaseCommands(manager);
        registerSkillCommands(manager);
        return manager;
    }

    private void registerConditions(PaperCommandManager manager) {
        manager.getCommandConditions().addCondition(Integer.class, "limits", (c, exec, value) -> {
            if (value == null) {
                return;
            }
            if (c.hasConfig("min") && c.getConfigValue("min", 0) > value) {
                throw new ConditionFailedException("Min value must be " + c.getConfigValue("min", 0));
            }
            if (c.hasConfig("max") && c.getConfigValue("max", 3) < value) {
                throw new ConditionFailedException("Max value must be " + c.getConfigValue("max", 3));
            }
        });
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
        contexts.registerContext(ManaAbility.class, c -> {
            String arg = c.popFirstArg();
            ManaAbility manaAbility = plugin.getManaAbilityRegistry().getOrNull(NamespacedId.fromDefault(arg));
            if (manaAbility == null || !manaAbility.isEnabled()) {
                Locale locale = plugin.getLocale(c.getSender());
                throw new InvalidCommandArgument(plugin.getMsg(CommandMessage.UNKNOWN_MANA_ABILITY, locale));
            }
            return manaAbility;
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
        contexts.registerContext(Trait.class, c -> {
            String arg = c.popFirstArg();
            Trait trait = plugin.getTraitRegistry().getOrNull(NamespacedId.fromDefault(arg));
            if (trait == null || !trait.isEnabled()) {
                Locale locale = plugin.getLocale(c.getSender());
                throw new InvalidCommandArgument(plugin.getMsg(CommandMessage.UNKNOWN_TRAIT, locale));
            }
            return trait;
        });
        contexts.registerContext(UUID.class, c -> {
            String input = c.popFirstArg();
            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                throw new InvalidCommandArgument(input + "is not a valid UUID!");
            }
        });
        contexts.registerContext(JsonArg.class, c -> {
            if (c.getArgs().isEmpty()) return null;

            var sb = new StringBuilder();
            int toAppend = 0;
            // Get the number of arguments part of JSON string
            for (String arg : c.getArgs()) {
                toAppend++;
                // Stop once JSON object terminator is reached
                if (arg.endsWith("}")) {
                    break;
                }
            }
            for (int i = 0; i < toAppend; i++) {
                sb.append(c.popFirstArg());
            }
            return new JsonArg(sb.toString());
        });
    }

    private String getSkillName(Skill skill) {
        if (skill instanceof CustomSkill) {
            return skill.getId().toString();
        } else {
            return skill.name().toLowerCase(Locale.ROOT);
        }
    }

    private void registerCompletions(PaperCommandManager manager) {
        var completions = manager.getCommandCompletions();
        completions.registerAsyncCompletion("skills", c -> {
            List<String> skills = new ArrayList<>();
            for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
                skills.add(getSkillName(skill));
            }
            return skills;
        });
        completions.registerAsyncCompletion("mana_abilities", c -> {
            List<String> abilities = new ArrayList<>();
            for (ManaAbility manaAbility : plugin.getManaAbilityManager().getEnabledManaAbilities()) {
                abilities.add(manaAbility.name().toLowerCase(Locale.ROOT));
            }
            return abilities;
        });
        completions.registerAsyncCompletion("skills_global", c -> {
            List<String> skills = new ArrayList<>();
            skills.add("global");
            for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
                skills.add(getSkillName(skill));
            }
            return skills;
        });
        completions.registerAsyncCompletion("stats", c -> {
            List<String> stats = new ArrayList<>();
            for (Stat stat : plugin.getStatManager().getEnabledStats()) {
                if (stat instanceof CustomStat) {
                    stats.add(stat.getId().toString());
                } else {
                    stats.add(stat.name().toLowerCase(Locale.ROOT));
                }
            }
            return stats;
        });
        completions.registerAsyncCompletion("traits", c -> plugin.getTraitManager().getEnabledTraits().stream()
                .map(t -> t instanceof CustomTrait ? t.getId().toString() : t.name().toLowerCase(Locale.ROOT))
                .toList());
        completions.registerAsyncCompletion("modifiers", c -> {
            Player player = c.getPlayer();
            User user = plugin.getUser(player);

            return user.getStatModifiers().keySet();
        });
        completions.registerAsyncCompletion("skill_top", c -> {
            List<String> values = new ArrayList<>();
            for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
                values.add(skill.getId().getKey());
            }
            values.add("average");
            return values;
        });
        completions.registerAsyncCompletion("lang", c -> plugin.getMessageProvider().getLanguageCodes());
        completions.registerAsyncCompletion("item_keys", c -> {
            List<String> keys = new ArrayList<>();
            for (NamespacedId id : plugin.getItemRegistry().getIds()) {
                if (id.getNamespace().equals(NamespacedId.AURASKILLS)) { // Add just the key if default namespace
                    keys.add(id.getKey());
                } else {
                    keys.add(id.toString());
                }
            }
            return keys;
        });
        completions.registerAsyncCompletion("sort_types", c -> {
            SortType[] sortTypes = SortType.values();
            List<String> typeNames = new ArrayList<>();
            for (SortType sortType : sortTypes) {
                typeNames.add(sortType.toString().toLowerCase(Locale.ROOT));
            }
            return typeNames;
        });
        completions.registerAsyncCompletion("menu_names", c -> plugin.getSlate().getLoadedMenus().keySet());
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
        manager.registerCommand(new PresetCommand(plugin));
        manager.registerCommand(new StorageCommand(plugin));
        manager.registerCommand(new OpenMenuCommand(plugin));
        manager.registerCommand(new ManaAbilityCommand(plugin));
        manager.registerCommand(new TraitCommand(plugin));
        manager.registerCommand(new JobsCommand(plugin));
        manager.registerCommand(new AntiAfkCommand(plugin));
    }

    public void registerSkillCommands(PaperCommandManager manager) {
        if (plugin.configBoolean(Option.ENABLE_SKILL_COMMANDS)) {
            Map<Skill, Boolean> map = plugin.getSkillManager().loadConfigEnabledMap();
            registerSkillCommand(new SkillCommands.FarmingCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.ForagingCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.MiningCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.FishingCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.ExcavationCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.ArcheryCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.DefenseCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.FightingCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.EnduranceCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.AgilityCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.AlchemyCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.EnchantingCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.SorceryCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.HealingCommand(plugin), map, manager);
            registerSkillCommand(new SkillCommands.ForgingCommand(plugin), map, manager);
        }
    }
    
    private void registerSkillCommand(SkillCommands.SkillCommand command, Map<Skill, Boolean> enabled, PaperCommandManager manager) {
        if (enabled.getOrDefault(command.skill, false)) {
            manager.registerCommand(command);
        }
    }

}
