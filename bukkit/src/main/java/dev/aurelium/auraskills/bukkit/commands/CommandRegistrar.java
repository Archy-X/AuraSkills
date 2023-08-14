package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.PaperCommandManager;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.commands.ManaCommand;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    }

    private void registerCompletions(PaperCommandManager manager) {
        var completions = manager.getCommandCompletions();
        completions.registerAsyncCompletion("skills", c -> {
            List<String> skills = new ArrayList<>();
            for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
                skills.add(skill.getId().toString());
            }
            return skills;
        });
        completions.registerAsyncCompletion("skills_global", c -> {
            List<String> skills = new ArrayList<>();
            skills.add("global");
            for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
                skills.add(skill.getId().toString());
            }
            return skills;
        });
        completions.registerAsyncCompletion("stats", c -> {
            List<String> stats = new ArrayList<>();
            for (Stat stat : plugin.getStatManager().getStatValues()) {
                stats.add(stat.getId().toString());
            }
            return stats;
        });
        completions.registerAsyncCompletion("modifiers", c -> {
            Player player = c.getPlayer();
            User user = plugin.getUser(player);

            return user.getStatModifiers().keySet();
        });
    }

    private void registerBaseCommands(PaperCommandManager manager) {
        manager.registerCommand(new SkillsRootCommand(plugin));
        manager.registerCommand(new SkillCommand(plugin));
        manager.registerCommand(new ManaCommand(plugin));
        manager.registerCommand(new ModifierCommand(plugin));
        manager.registerCommand(new ItemCommand(plugin));
        manager.registerCommand(new ArmorCommand(plugin));
    }

}
